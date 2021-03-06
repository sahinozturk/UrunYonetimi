package com.example.java_oglen.urunyonetimi;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AnaKategori.OnFragmentInteractionListener,  Sepetim.OnFragmentInteractionListener, Profil.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Ayarlar Bölümü Tıklandı", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fgt = null;
        Class fgtClass = null;
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            // ana kategori fregment açılıyor
            fgtClass = AnaKategori.class;
        } else if (id == R.id.nav_gallery) {
Log.e("x","favori ürünler sayfasına girildi");
            Intent i =new Intent(MainActivity.this,FavoriUrunlerSayfasi.class);
            startActivity(i);

        } else if (id == R.id.nav_slideshow) {
            fgtClass = Sepetim.class;

        } else if (id == R.id.nav_manage) {
            fgtClass = Profil.class;

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }else if (id == R.id.nav_cikis) {
            // uyarı göster
            AlertDialog.Builder uyari = new AlertDialog.Builder(this);
            uyari.setTitle("Çıkış Yap");
            uyari.setMessage("Çıkış Yapmak istediğinizden eminmisiniz ?");
            uyari.setCancelable(true);
            // evet button
            uyari.setPositiveButton("Çıkış Yap", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MainActivity.this, "Çıkış Yapılsın", Toast.LENGTH_SHORT).show();
                }
            });

            uyari.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MainActivity.this, "İptal Edildi", Toast.LENGTH_SHORT).show();
                }
            });

            AlertDialog alt = uyari.create();
            alt.show();

        }


            try {
                fgt = (Fragment) fgtClass.newInstance();
                FragmentManager mng = getSupportFragmentManager();
                mng.beginTransaction().replace(R.id.flContent, fgt).commit();
            } catch (Exception ex) {
                Log.e("Fragment Hatası ", ex.toString());
            }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    int tiklama = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK  && event.getRepeatCount() == 0) {
            tiklama++;
            if(tiklama == 2) {
                AlertDialog.Builder uyari = new AlertDialog.Builder(this);
                uyari.setMessage("Lütfen Çıkma");
                uyari.setTitle("Çıkma İşlemi");
                uyari.setCancelable(true);
                uyari.setPositiveButton("Çıkış Yap", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       System.exit(0);
                    }
                });
                uyari.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        tiklama = 0;
                    }
                });
                uyari.show();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
