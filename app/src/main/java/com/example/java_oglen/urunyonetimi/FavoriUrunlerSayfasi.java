package com.example.java_oglen.urunyonetimi;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FavoriUrunlerSayfasi extends AppCompatActivity {


    TextView tvbaslik,tvfiyat;
    ListView lview;
    BaseAdapter ba;
    LayoutInflater li;
    Boolean favoriyok=true;
    private View view;
    ArrayList<String> baslikim=new ArrayList<>();
    ArrayList<String> fiyatim=new ArrayList<>();
    String [] fiyat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favori_urunler_sayfasi);
Log.e(" kısım","1");

        SQLiteDatabase fOku = new DB(this.getApplicationContext()).getReadableDatabase();


        //query ile sorgumuzu gerçekleştiririz standart sql sorgusu
        String query = "SELECT * FROM favoriler";

        //sql data base bize cursor yapsısında bir veri dönüşü yapacaktır.Yapmış olduğumuz bu sorguyu belirtiriz
        // Cursor Veritabanı nesneleri içerisinde satır bazlı hareket etmemizi sağlar
        Cursor cr = fOku.rawQuery(query, null);

        //Database de ulaşmak istediğim verilere ulaşmamı sağlıyor
        int uid = cr.getColumnIndex("kid");
        int ubaslik = cr.getColumnIndex("ubaslik");
        int ufiyat = cr.getColumnIndex("ufiyat");


        //data base dolu ise ekrana birşeyler yazdırıyoruz
        if (cr != null) {

            //çektiğimiz verileri sırayla okumamızı sağlıyor
            while (cr.moveToNext()) {

                String baslik=cr.getString(ubaslik);
                Log.e("eklenen favori başlığı ", cr.getString(ubaslik));
                Log.e("eklenen favori fiyat", cr.getString(ufiyat));

                baslikim.add(baslik);
                fiyatim.add(cr.getString(ufiyat));

            }
            Log.e("list sayısı ",""+baslikim.size());

            lview = (ListView) findViewById(R.id.lvfavori);
            li=LayoutInflater.from(FavoriUrunlerSayfasi.this);

            ba=new BaseAdapter() {
                @Override
                public int getCount()
                {
                    return baslikim.size();
                }

                @Override
                public Object getItem(int position) {
                    return null;
                }

                @Override
                public long getItemId(int position) {
                    return 0;
                }

                @Override
                public View getView(int i, View view, ViewGroup parent)
                {
                    if(view==null)
                    {
                    view=li.inflate(R.layout.item,null);
                    }
                    else
                    {
                        Log.e("olusturulmuş","view favori ürünler");
                    }

                    tvbaslik= (TextView) view.findViewById(R.id.item_baslik);
                    tvfiyat= (TextView) view.findViewById(R.id.item_fiyat);

                 tvbaslik.setText(baslikim.get(i));
                 tvfiyat.setText(fiyatim.get(i));

                    return view;
                }
            };
            lview.setAdapter(ba);


        }
    }
}
