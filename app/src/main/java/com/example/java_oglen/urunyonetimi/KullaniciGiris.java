package com.example.java_oglen.urunyonetimi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class KullaniciGiris extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kullanici_giris);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    /**Kullanıcı kayıtlı degilse ve kayıt yapırması gerekiyorsa
     * Kayıt ekranına geçiş yapması gerekmektedir
     * bu yüzden aşağıdaki metodu kullanırız
     * */
    public void kayitYap(View v) {
        Intent i = new Intent(KullaniciGiris.this, KayitYap.class);
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
