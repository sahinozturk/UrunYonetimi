package com.example.java_oglen.urunyonetimi;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;

public class KayitYap extends AppCompatActivity {

    EditText ad,soyad,mail,sifre,telefon;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_yap);

        ad=(EditText)findViewById(R.id.txadınız);
        soyad=(EditText)findViewById(R.id.txsoyadi);
        sifre=(EditText)findViewById(R.id.txsifre);
        mail=(EditText)findViewById(R.id.txemail);
        telefon=(EditText)findViewById(R.id.txtelefon);


    }

    public void kayitEkle(View view)
    {
        String ad=this.ad.getText().toString();
        String soyad=this.soyad.getText().toString();
        String sifre=this.sifre.getText().toString();
        String mail=this.mail.getText().toString();
        String telefon=this.telefon.getText().toString();


        //Kullanıcı bilgilerinin tamımını girmiş mi diye sorguluyoruz
        if (!ad.isEmpty() && !soyad.isEmpty() && !sifre.isEmpty() && !mail.isEmpty() && !telefon.isEmpty())
        {
            //Json blut hesabına kullanıcı kayıt için girilmesi gereken bilgileri hazırlıyoruz
            //Bu url girildiği zaman bilgilerde eksiklik veya yanlışlık yok ise
            //kayıt işlemi gerçekleşilecektir
            String url="http://jsonbulut.com/json/userRegister.php?ref=cb226ff2a31fdd460087fedbb34a6023&"+
                    "userName="+ad+"&" +
                    "userSurname="+soyad+"&" +
                    "userPhone="+telefon+"&" +
                    "userMail="+mail+"&" +
                    "userPass="+sifre+"";




            //Doldurulan alanları temizliyoruz
            this.ad.setText("");
            this.soyad.setText("");
            this.telefon.setText("");
            this.mail.setText("");
            this.sifre.setText("");

            //bu bilgileri aldıktan sonra asıl çalışacak gövdemizi çağırarak bu metotla olan işimizi bitiriyoruz
            new jsonverileri(url,KayitYap.this).execute();
        }
        else
        {
            Toast.makeText(this,"Lütfen tüm bilgileri doldurunuz",Toast.LENGTH_SHORT).show();
        }

    }
/**Asıl  işlemleri gerçekleştirdiğim metotdur
 * Dikkat edilmesi gereken bu gövdeye AsyncTask extend ederek oluşturulmuş olması
 * bu işlem bize java thread yapısı sağlayarak jsona bağlanırken uygulamanın donmasını engelliyor
 * */
    static class  jsonverileri extends AsyncTask<Void,Void,Void>
    {
        String url = "";
        String data = "";
        Context cnx;
        ProgressDialog pro;
        public jsonverileri (String url, Context cnx)
        {
            this.url = url;
            this.cnx = cnx;
            pro = new ProgressDialog(cnx);
            pro.setMessage("İşlem yaplıyor. Lütfen Bekleyiniz.");
            pro.show();
        }

        //doınBackground yapısı AsyncTask yapısından Override edilmiştir
        //
        @Override
        protected Void doInBackground(Void... voids)
        {
            try {
                data=Jsoup.connect(url).ignoreContentType(true).
                        get().body().text();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //Bu kısımda kayıt işleminin gerçekleşip gerçekleşmediğini ögreniyoruz
        //Bize dönüş yapan durum değerine göre kayıt gerçekleşti veya gerçekleşmedi diyerek işlemler gerçekleştiriyoruz
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try
            {
                JSONObject obj=new JSONObject(data);
                boolean durum=obj.getJSONArray("user").getJSONObject(0).getBoolean("durum");

                String mesaj = obj.getJSONArray("user").getJSONObject(0).getString("mesaj");
                if (durum)
                {
                    Toast.makeText(cnx, mesaj, Toast.LENGTH_SHORT).show();
                    String kid =  obj.getJSONArray("user").getJSONObject(0).getString("kullaniciId");
                    Log.e("kid", kid);

                }
                else
                {
                    Toast.makeText(cnx, mesaj, Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
                Log.e("kayıtet","hata oluştu");
            }
            pro.dismiss();

        }
    }


}
