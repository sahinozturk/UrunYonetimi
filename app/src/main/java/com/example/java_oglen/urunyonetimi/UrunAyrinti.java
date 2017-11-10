package com.example.java_oglen.urunyonetimi;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.HashMap;

public class UrunAyrinti extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    static JSONObject uobj;
    private SliderLayout mDemoSlider;

    // urun ayrıntı elemanları
    TextView baslik, urunFiyat;
    WebView aciklama;

    //Kullanıcı ID bilgilerini tutmak için kullanıyoruz bu
    //Id bilgilerini kullanarak kullanıcı giriş yapmışmı onu sorgulayacağız
    SharedPreferences sha;
    SharedPreferences.Editor edit;

    //Bütün program içerisinde kullanıcı ID için kid diye bir değişken tanımladık
    //Buradaki amacımız kolaylıkla kullanıcı ID bilgisini sorgulayabilmek
    String kid = "";

    //Favorilere ekleme işlemini ToogleButton ile gerçekleştirdik
    ToggleButton btnFavoriEkle;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urun_ayrinti);

        // sharedPreferences üzerinde işlemler yaparken başka App bizim bilgilerimizi
        //Göremesin diye Mode_Private kullanıyoruz
        // urunxml bizim bilgilerimizi yazacağımız dosyanın adı
        sha = getSharedPreferences("urunxml", MODE_PRIVATE);

        //edit elemanı bizim data yazmamız için gerekli olan elemandır
        edit = sha.edit();

        //getString elemanı ile kid içerisine bir kid elemanı beklediğimibelirten ifade
        // ilk key stringidir, defvalue değeri ise istediğin bilgi gelmez ise ne atansın diye sorar
        //Bu durumda kid gelmez ise yerine 1 atansın denmiştir
        // kid degeri boşsa biz kullanıcının giriş yapmamış olduğunu anlarız
        kid = sha.getString("kid","1");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Favoriler butonu olarak tanımladığımız btnFavoriEkle proğrama tanıtıyoruz
        btnFavoriEkle = (ToggleButton) findViewById(R.id.btnFavoriEkle);
        // daha önce favori var mı ?
        try
        {
            //Bu kısımda sql database dosyamızda sorgulama yapıyoruz
            //amacımız daha önceden veri kaydımız olduysa program kullanımı sırasında veya
            //yeniden girişler sırasında belirlediğimiz favorileri almaktır.
            SQLiteDatabase fOku =  new DB(getApplicationContext()).getReadableDatabase();

            //query ile sorgumuzu gerçekleştiririz standart sql sorgusu
            String query = "SELECT * FROM favoriler WHERE uid = '"+uobj.getString("productId")+"' ";

            //sql data base bize cursor yapsısında bir veri dönüşü yapacaktır.Yapmış olduğumuz bu sorguyu belirtiriz
            Cursor cr = fOku.rawQuery(query, null);

            //moveToNext eger sonrasında bir satır varsa yani okunacak bir değer varsa çalışır
            //okunacak değer varsa demek oluyor ki sql içerisinde yazılı veriler var
            //bu verileri kullanarak favori butonlarının arka planını dolu kalp yapacağız
            if(cr.moveToNext())
            {   //eklenen ürünün favorisini dolu kalp yap
                btnFavoriEkle.setBackgroundResource(R.drawable.dolukalp);

            }
            else
            {
                //bu durumda daha önce hiç seçim olmamış demek oluyor hepsini boş kalp yap
                btnFavoriEkle.setBackgroundResource(R.drawable.boskalp);
            }
        }
        catch (Exception ex)
        {
            Log.e("Favori Sorgu Hatası ", ex.toString());
        }


        // favorilere tıklanıp tıklanmadığını algılamak için
        //btnFavoriEkle butonuna dinleyici oluşturduk
        btnFavoriEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //Önce bak bu kullanıcının id bilgisi varmı
                //yani kullanıcı girişi yapmışmı eğer yapmışsa favoriyi ekle bölümünü kullanabilsin
                //Eğer kullanıcı girişi yapmamışsa favori özzelliğini kullanamasın kullanıcı giriş sayfasına girsin
                if(kid.equals("1"))
                {
                    Intent i =  new Intent(UrunAyrinti.this, GirisEkrani.class);
                    finish();
                    startActivity(i);

                }
                else
                {
                    if (btnFavoriEkle.isChecked())
                    {


                        try
                        {
                            //Veri tabanına ulaşıyoruz
                            //getApplicationContext bizim için burada this anahtarı görevini üstleniyor
                            // başka bir view gövdesinin içinde bazen this anahtarıyla ulaşılamayan gövdeye bu yöntemle ulaşabiliriz
                            SQLiteDatabase sil = new DB(getApplicationContext()).getWritableDatabase();

                            //Silme işlemi için delete çalıştırılır burada ilk olarak tablonun adını
                            //sonra silmek istediğimiz satırın id değerini yazarız burada "uid=?" yapısı kullanılır
                            // sonrada içinde bizim uid çağırmak için kullandığımız yapıdan oluşan bir string array ister

                            int silSonuc = sil.delete("favoriler", "uid=?", new String[]{uobj.getString("productId") });
                            sil.close();

                            //Buradada eğer favorilerden ürün kaldırma işlemi olmuş ise long silsonuc değeri 0 dan farklı bir değer alacaktır
                            //Bu durumda favoriler arka planı değişmelidir
                            if (silSonuc > 0)
                            {
                                //Eğer favorilerden çıkarıldıysa arka plan boş kalp olsun
                                btnFavoriEkle.setBackgroundResource(R.drawable.boskalp);
                                Toast.makeText(UrunAyrinti.this, "Ürün favorilerden çıkarıldı", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception ex)
                        {
                            Log.e("Silme Hatası", ex.toString());
                        }
                        //Toast.makeText(UrunAyrinti.this, "Seçili", Toast.LENGTH_SHORT).show();


                    }
                    else
                    {
                        // veritabanına urunu yaz
                        try
                        {
                            //bu sınıfta çalıştır ve bu verileri veri tabanına yaz
                            SQLiteDatabase yaz = new DB(getApplicationContext()).getWritableDatabase();

                            //ContentValues bizim dosyaya veriyi ulaştırmamızı sağlıyor
                            // anahtar kelime ve değeri olmak üzere iki parametre yazılır bu parametreleri tablomuza ekler
                            //ve bu anahtar kelimeler sayesindede istediğimiz zaman çekebiliriz
                            ContentValues data = new ContentValues();

                            //Eklemek istediğimiz değerleri yukarıda tanımladığımız data ile put özelliğini kullanarak
                            //ekleme işlemi yaparız
                            data.put("uid", uobj.getString("productId"));
                            data.put("kid", kid);
                            data.put("ubaslik", uobj.getString("productName"));
                            data.put("ufiyat",uobj.getString("price") );

                            //long tipinde tabloya ekleme yapacağız
                            // yaz.insert ile önce tablonun adını
                            //sonra colon bilgisini null olarak
                            //sonrada context olan bilgilerimizi yani yukarıda oluşturduğumuz data verisini atıyoruz
                            long yazmaSonuc = yaz.insert("favoriler",null, data);

                            //veri tabanını işlem bitince kapat
                            yaz.close();

                            //Eger yazma işlemi başarılı ise yani long değerine  0 dan farklı  değer atanmışsa yazma işlemi
                            //başarılı olmuştur bu durumda favorilere ekleme işlemi başarılı olmuştur
                            if(yazmaSonuc > 0)
                            {
                                //Eğer favorilere eklendi ise buton arka plan dolu kalp olsun
                                btnFavoriEkle.setBackgroundResource(R.drawable.dolukalp);
                                Toast.makeText(UrunAyrinti.this, "Ürün favorilere eklendi !", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(UrunAyrinti.this, "Ekleme işlemi başarısız oldu !", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception ex)
                        {
                            Log.e("Yazma Hatası : ", ex.toString());
                        }
                   }
                }
            }
        });
        baslik = (TextView) findViewById(R.id.txtUrunBaslik);
        urunFiyat = (TextView) findViewById(R.id.txtUrunfiyat);
        aciklama = (WebView) findViewById(R.id.webUrunAciklama);



        mDemoSlider = (SliderLayout)findViewById(R.id.slider);
        HashMap<String,String> url_maps = new HashMap<String, String>();

        //Log.d("Urun Data", ""+uobj);
        try
        {
            // dataları yerleştirme
            baslik.setText(uobj.getString("productName"));
            urunFiyat.setText(uobj.getString("price"));
            String webAciklama = Jsoup.parse(uobj.getString("description")).text();
            aciklama.getSettings().setJavaScriptEnabled(true);
            aciklama.getSettings().setLoadWithOverviewMode(true);
            aciklama.setWebViewClient(new WebViewClient());
            aciklama.setBackgroundColor(0x00000000);
            //String webYaz = Html.fromHtml(webAciklama).toString();

            // video kontrol sağlanıyor
            String video = uobj.getString("campaignTitle");
            if(!video.equals(""))
            {
                String embed = "<!DOCTYPE html> <html> <head> <title> </title> </head> <body> <style type=\"text/css\"> body { font: Arial; } </style> <hr><h4>Ürün Videosu</h4><div><iframe style=\"width: 100%; height: 100%;\" src=\"https://www.youtube.com/embed/\"+video+\"\" frameborder=\"0\" allowfullscreen></iframe></div> </body> </html>";
                webAciklama += embed;
            }

            aciklama.loadDataWithBaseURL("", webAciklama , "text/html",  "UTF-8", "");

            // resim var mı ?
            boolean rDurum = uobj.getBoolean("image");
            if(rDurum)
            {
                JSONArray resimArr = uobj.getJSONArray("images");
                for(int i = 0; i< resimArr.length(); i++)
                {
                    String ritem = resimArr.getJSONObject(i).getString("normal");
                    url_maps.put("Urun " + i, ritem);
                }

                for(String name : url_maps.keySet())
                {
                    TextSliderView textSliderView = new TextSliderView(this);
                    // initialize a SliderLayout
                    textSliderView
                            .description(name)
                            .image(url_maps.get(name))
                            .setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
                            .setOnSliderClickListener(this);

                    //add your extra information
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("extra",name);

                    mDemoSlider.addSlider(textSliderView);
                }
                mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                mDemoSlider.setCustomAnimation(new DescriptionAnimation());
                mDemoSlider.setDuration(4000);
                mDemoSlider.addOnPageChangeListener(this);
            }
        }
        catch (Exception ex)
        {
            Log.e("Json Hatası ", ex.toString() );
        }

        // mDemoSlider.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
    }

    @Override
    protected void onStop()
    {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider)
    {
        Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {}

    @Override
    public void onPageSelected(int position)
    {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {}

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
