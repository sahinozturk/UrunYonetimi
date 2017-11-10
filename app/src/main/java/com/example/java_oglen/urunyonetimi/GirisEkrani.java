package com.example.java_oglen.urunyonetimi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.IOException;


public class GirisEkrani extends AppCompatActivity {

    EditText etkulanicimail,etsifre;
    String mail,sifre;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris_ekrani);


    }


    public void girisek(View view)
    {
        //kullanıcıdan girilen mail ve şifre bilgileri alınır
       etkulanicimail=(EditText)findViewById(R.id.txtGirisAdi);
        etsifre=(EditText)findViewById(R.id.txtGirisSifre);

        //edit text ten dönen veri string değil bu yüzden veriyi stringe çevirmemiz gerek
        mail=etkulanicimail.getText().toString();
        sifre=etsifre.getText().toString();

        //bilgileri çekeceğimiz json adresine giriş için gerekli parametreleri url içerisine gömeriz
        String url="http://jsonbulut.com/json/userLogin.php?ref=cb226ff2a31fdd460087fedbb34a6023&" +
                "userEmail="+mail+
                "&userPass="+sifre+
                "&face=no";

        //ekrande şifremizin ve kullanıcı adımızın kalmaması için silme işlemi yapılır
        etsifre.setText("");
        etkulanicimail.setText("");



        new jsonverileri(url,GirisEkrani.this).execute();
    }

    public void opKayiy(View view)
    {
        Intent kayitE=new Intent(GirisEkrani.this,KayitYap.class);
        startActivity(kayitE);

    }


    class  jsonverileri extends  AsyncTask<Void,Void,Void>
    {
        String url = "";
        String data = "";
        Context cnx;

        ProgressDialog pro;
        public jsonverileri(String url, Context cnx)
        {
            this.url = url;
            this.cnx = cnx;
            pro = new ProgressDialog(cnx);
            pro.setMessage("İşlem yaplıyor. Lütfen Bekleyiniz.");
            pro.show();
            Log.e("x","Burasi ikinci kısım");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... voids)
        {
            try
            {
                // Adresden data yı çekeceğiz bu datayı OnPostExecute
                data= Jsoup.connect(url).ignoreContentType(true)
                        .get().body().text();


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }
        @Override

         protected void onPostExecute(Void aVoid)
        {

        super.onPostExecute(aVoid);

        try
        {
            Log.e("Gelen Data", data);
            //adrese ulaştığımızda data içerisine aktardığımız verilerin bir json array olduğunu gördük
            //bu yüzden verileri çekmek için json arrayi object olarak çekiyoruz
            JSONObject obj=new JSONObject(data);

            //çektiğimiz verileri log ekranına basıyoruz
            Log.e("Gelen obje", obj.toString());

            // çektiğimiz veri doğru ise girilmiş ise json bize true dönderiyor o yüzden önce
            //boolean ile sonucun true mu yoksa false mi döndüğünü öğrenmeliyiz
            //Çekilen veri boolean olduğu için veri türünüde boolean olarak belirliyoruz
            boolean durum=obj.getJSONArray("user").getJSONObject(0).getBoolean("durum");

            //Jsonun içerisinde user->içerisinde jsonobjenin birinci elemanı->onun içerisindeki jsonobjenin bilgiler bölümünde
            //-> string olarak yer alan username yi çekiyoruz
            // Sonraki adımlar içinde aynı işlemi yapıyoruz
            String adi= obj.getJSONArray("user").getJSONObject(0).getJSONObject("bilgiler").getString("userName");
            String soyadi= obj.getJSONArray("user").getJSONObject(0).getJSONObject("bilgiler").getString("userSurname");
            String telefon=obj.getJSONArray("user").getJSONObject(0).getJSONObject("bilgiler").getString("userPhone");


            if (durum)
            {

                Toast.makeText(cnx, "", Toast.LENGTH_SHORT).show();
                Toast.makeText(cnx, "Profil sayfasına yönlendiriliyorsunuz", Toast.LENGTH_SHORT).show();
                Intent in=new Intent(GirisEkrani.this,Profil.class);
              //  in.putExtra("mail",mail);
               // in.putExtra("kisitelefon",telefon);
               // in.putExtra("kisiadi",adi);
               // in.putExtra("kisisoyadi",soyadi);
                startActivity(in);
                finish();
                Log.e("x","Buraya girdimi");
            }
            else
            {
                Toast.makeText(cnx, "Girilen bilgiler yanlış", Toast.LENGTH_SHORT).show();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        pro.dismiss();

        }

    }





}
