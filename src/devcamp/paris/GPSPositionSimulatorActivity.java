package devcamp.paris;

import java.util.List;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;



public class GPSPositionSimulatorActivity extends MapActivity {
    /** Called when the activity is first created. */
	
	private MapView mapView;
	private MyItemizedOverlay itemizedoverlay;
	private OverlayItem overlayitem; 

	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        Location l = getPositionGeoloc();
		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
		itemizedoverlay = new MyItemizedOverlay(drawable, this);
		GeoPoint point = new GeoPoint((int) (Double.valueOf(l.getLatitude()) * 1E6),(int) (Double.valueOf(l.getLongitude()) * 1E6));
		overlayitem = new OverlayItem(point, "PingMe", "");
		itemizedoverlay.addOverlay(overlayitem);
		mapOverlays.add(itemizedoverlay);
		navigateToLocation(point, mapView);
		mapView.invalidate();
		
        mapView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                 //return onTouchEvent(motionEvent);
                //---when user lifts his finger---
                if (motionEvent.getAction() == 0) {                
                    GeoPoint p = mapView.getProjection().fromPixels((int) motionEvent.getX(),(int) motionEvent.getY());
                    OverlayItem overlayitem = new OverlayItem(p, "PingMe", "");
                    List<Overlay> mapOverlays = mapView.getOverlays();
                    itemizedoverlay.removeOverlay();
            		itemizedoverlay.addOverlay(overlayitem);
            		mapOverlays.remove(0);
            		mapOverlays.add(itemizedoverlay);
            		navigateToLocation(p, mapView);
            		mapView.invalidate();
            		
            		Intent serviceIntent = new Intent( "com.pingme.PingMeService.PING_ACTION_MOCK_LOCATION" );   
            		serviceIntent.setClassName("com.pingme", "com.pingme.PingMeService");
//            		
//            		serviceIntent.setClassName( GPSPositionSimulatorActivity.this, "com.pingme.PingMeService" );     
            		serviceIntent.putExtra("lat", p.getLatitudeE6()/1E6);
            		serviceIntent.putExtra("lng", p.getLongitudeE6()/1E6);
            		startService( serviceIntent );
            		 
                }                            
                return true;
            }
        });
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
public void navigateToLocation(GeoPoint gp,MapView mv) {
		// GeoPoint
		MapController mc = mv.getController();
		mc.setZoom(16);
		mc.animateTo(gp);
		mv.invalidate();
	}   

	private Location getPositionGeoloc() {
		LocationManager lManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		List <String> providers = lManager.getProviders(true);
		String myLocationManager= null;
		Location location = null;
		if(!providers.isEmpty()){
		myLocationManager = providers.get(0);
		location = lManager.getLastKnownLocation(myLocationManager);
		}
		return location;
	}
}