package com.example.leeje.androidpresentsystem;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.nhn.android.maps.NMapController;
import com.nhn.android.maps.NMapOverlayItem;
import com.nhn.android.maps.NMapView;
import com.nhn.android.maps.NMapView.OnMapStateChangeListener;
import com.nhn.android.maps.maplib.NGeoPoint;
import com.nhn.android.maps.nmapmodel.NMapError;
import com.nhn.android.maps.overlay.NMapPOIdata;
import com.nhn.android.maps.overlay.NMapPOIitem;
import com.nhn.android.mapviewer.overlay.NMapOverlayManager;
import com.nhn.android.mapviewer.overlay.NMapPOIdataOverlay;
import com.nhn.android.mapviewer.overlay.NMapResourceProvider;


/**
 * A simple {@link Fragment} subclass.
 */
public class Map extends NMapFragment implements NMapView.OnMapStateChangeListener{

    private static final String NAVER_CLIENT_ID="BLqBZK9lmPHKaFgf2WYv";
    public Map() {
        // Required empty public constructor
    }
    private NMapView nMapView;
    private NMapController mapController;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        nMapView = new NMapView(getActivity());
        nMapView.setClientId(NAVER_CLIENT_ID);
        nMapView.setClickable(true);

        return nMapView;
    }

    private NMapOverlayManager mOverlayManager;
    private NMapResourceProvider mMapViewerResourceProvider;
    @Override
    public void onStart() {
        super.onStart();
        nMapView.setOnMapStateChangeListener(this);
        nMapView.setBuiltInZoomControls(true,null);
        mapController=nMapView.getMapController();



        mOverlayManager=new NMapOverlayManager(getActivity(),nMapView,null);

    }

    @Override
    public void onMapInitHandler(NMapView nMapView, NMapError nMapError) {
        if(nMapError==null)
        {
            nMapView.setBuiltInZoomControls(true,null);
            mapController.setMapCenter(new NGeoPoint(126.978371,37.5666091),11);

        }
    }

    @Override
    public void onMapCenterChange(NMapView nMapView, NGeoPoint nGeoPoint) {

    }

    @Override
    public void onMapCenterChangeFine(NMapView nMapView) {

    }

    @Override
    public void onZoomLevelChange(NMapView nMapView, int i) {

    }

    @Override
    public void onAnimationStateChange(NMapView nMapView, int i, int i1) {

    }


}