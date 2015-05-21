package com.example.cristhian.prototipo2;

import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class Sitios extends ActionBarActivity{

    private String [] opciones;
    private DrawerLayout drawerLayout;
    private ListView listView;
    private ActionBarDrawerToggle drawerToggle;

    private CharSequence tituloSec;
    private CharSequence tituloApp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitios);

        opciones = new String[]{"Lugares recientes","Centros Comerciales", "Restaurantes", "Mas"};
        drawerLayout = (DrawerLayout) findViewById(R.id.contenedor_principal);
        listView = (ListView) findViewById(R.id.menuizquierdo);

        listView.setAdapter(new ArrayAdapter<String>(getSupportActionBar().getThemedContext(),
                android.R.layout.simple_list_item_1, opciones));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = null;
                switch (position){
                    case 0:
                        fragment = new Recientes();
                        findViewById(R.id.mostrarAdentro).setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        fragment = new Centro_comercial();
                        findViewById(R.id.mostrarAdentro).setVisibility(View.GONE);
                        break;
                    case 2:
                        fragment = new Restaurante();
                        findViewById(R.id.mostrarAdentro).setVisibility(View.GONE);
                        break;
                    case 3:
                        fragment = new Mas();
                        findViewById(R.id.mostrarAdentro).setVisibility(View.GONE);
                        break;
                }


                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.contenedor_frame,fragment)
                        .commit();

                listView.setItemChecked(position,true);
                tituloSec = opciones [position];
                getSupportActionBar().setTitle(tituloSec);
                drawerLayout.closeDrawer(listView);


            }
        });

        tituloSec =getTitle();
        tituloApp =getTitle();

        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,
                R.string.abierto,R.string.cerrado){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                ActivityCompat.invalidateOptionsMenu(Sitios.this);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                ActivityCompat.invalidateOptionsMenu(Sitios.this);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);






    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        int id =item.getItemId();


        if(id==R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

}
