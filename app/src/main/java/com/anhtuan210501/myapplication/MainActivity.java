package com.anhtuan210501.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final String TAG = "DijkstraAlgorithm";
    GoogleMap map;
    test a = new test();
    boolean isPermissionsGranted;
    private DatabaseReference myRef;
    LocationRequest locationRequest;
    List<Address> addressList = null;
    List<Integer> instruction_list;
    List<Integer> path_backup, cut_node_save, path_cut_save;
    List<LatLng> backup_list;
    FusedLocationProviderClient fusedLocationProviderClient;
    Handler handler = new Handler();
    Runnable runnable;

    private Node startNode;
    private Node endNode;
    private Graph graph;
    private Graph graph2;
    List<Node> path2= new ArrayList<>();
    private float close_distance = Float.MAX_VALUE;
    private float close_distance2 = Float.MAX_VALUE;
    private float close_distance3 = Float.MAX_VALUE;
    private float close_distance4 = Float.MAX_VALUE;
    private int close_node = 100;
    private int close_node2 = 100;
    private int close_node3 = 100;
    private int close_node4 = 100;
    private int delay=1000, check_waypoint=0, check = 0, check2=0, change_road=0,
            check_circle=0, cut=0, cut_save=0, reverse=0, lock_check=0, check_route=0, check_change=0;
    private double lat_location=0, lng_location=0, lat_des=10.851201963938433, lng_des=106.77314478904007, total_dis=0;
    Switch lock, change, update;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Button btn = findViewById(R.id.btn_code);
        update = findViewById(R.id.switch_test);
        change = findViewById(R.id.switch_test2);
        lock = findViewById(R.id.switch_lock);
        path_backup = new ArrayList<>();
        cut_node_save = new ArrayList<>();
        path_cut_save = new ArrayList<>();
        checkpermission();
        if (checkggService()) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            if (isPermissionsGranted) {
                checkGPS();
            }
        } else {
            Toast.makeText(MainActivity.this, "GG PLAY NOT AVAILABLE", Toast.LENGTH_SHORT).show();
        }
        myRef = FirebaseDatabase.getInstance().getReference();myRef = FirebaseDatabase.getInstance().getReference();
        update.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    check = 1;
                }else{
                    check = 0;
                }
            }
        });
        change.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    check2 = 1;
                }else{
                    check2 = 0;
                }
            }
        });
        change.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                XacnhanxoaMaker();
                return false;
            }
        });
        lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    lock_check = 1;
                }else{
                    lock_check = 0;
                }
            }
        });
        myRef.child("/change").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Integer.class) != null) {
                    change_road = dataSnapshot.getValue(Integer.class);
                    if (change_road==1){
                        lock_check=0;
                        lock.setChecked(false);
                        doiduong();
                    }else{
                        myRef.child("/change").setValue(change_road);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        //10.852366, 106.772798
        LatLng latLng = new LatLng(10.851293, 106.772351);
        LatLng latLng1 = new LatLng(10.853375, 106.773111);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            checkpermission();
            return;
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setOnMapLongClickListener(this);
        //10.851422, 106.771340,10.851249, 106.772658,10.850121, 106.772523
        //double angle = goc_vo_huong(10.851422, 106.771340,10.851153, 106.771311,10.853683, 106.771619);
    }
    private void createGraph(double lat_location, double lng_location, double lat_des, double lng_des, int change,
                             int cutnode, int rev) {
        ArrayList<Node> nodes = new ArrayList<>();
        ArrayList<Edge> edges = new ArrayList<>();
        map.clear();
        backup_list = new ArrayList<>();
        //start và end
        // Tạo các đối tượng Node và thêm vào danh sách các đỉnh
        Node node1 = new Node(new LatLng(10.851422, 106.771340), 1);//addMarker(10.851422, 106.771340);
        Node node2 = new Node(new LatLng(10.853683, 106.771619), 2);//addMarker(10.853683, 106.771619);
        Node node3 = new Node(new LatLng(10.853561, 106.772527), 3);//addMarker(10.853561, 106.772527);
        Node node4 = new Node(new LatLng(10.853313, 106.772594), 4);//addMarker(10.853313, 106.772594);
        Node node5 = new Node(new LatLng(10.853242, 106.772889), 5);//addMarker(10.853242, 106.772889);
        Node node6 = new Node(new LatLng(10.852822681565097, 106.77284874022007), 6);//addMarker(10.852832, 106.772851);
        Node node7 = new Node(new LatLng(10.852744642333589, 106.77343882620335), 7);//addMarker(10.852743, 106.773430);
        Node node8 = new Node(new LatLng(10.852364324281616, 106.77279576659203), 8);//addMarker(10.852366, 106.772798);
        Node node9 = new Node(new LatLng(10.852266198852275, 106.77338853478432), 9);//addMarker(10.852277, 106.773382);
        Node node10 = new Node(new LatLng(10.851257283173354, 106.7726569622755), 10);//addMarker(10.851249, 106.772658);
        Node node11 = new Node(new LatLng(10.85117628000445, 106.77329029887915), 11);//addMarker(10.851182, 106.773321);
        Node node12 = new Node(new LatLng(10.850132, 106.773216), 12);//addMarker(10.850132, 106.773216);
        Node node13 = new Node(new LatLng(10.850121, 106.772523), 13);//addMarker(10.850121, 106.772523);
        Node node14 = new Node(new LatLng(10.850125, 106.771604), 14);//addMarker(10.850125, 106.771604);
        Node node15 = new Node(new LatLng(10.850580, 106.771263), 15);//addMarker(10.850580, 106.771263);
        Node node16 = new Node(new LatLng(10.850742, 106.771265), 16);//addMarker(10.850742, 106.771265);
        Node nodeStart = new Node(new LatLng(lat_location, lng_location
        ), 17);//addMarker(lat_location, lng_location);
        Node nodeEnd = new Node(new LatLng(lat_des, lng_des
        ), 18);addMarker(lat_des, lng_des);

        // việc sắp xếp thứ tự thêm vào node sẽ ảnh hưởng đến thuật toán tìm đường, luôn để vị trí bắt đầu ở đầu tiên
        nodes.add(nodeStart);
        nodes.add(node1);
        nodes.add(node2);
        nodes.add(node3);
        nodes.add(node4);
        nodes.add(node5);
        nodes.add(node6);
        nodes.add(node7);
        nodes.add(node8);
        nodes.add(node9);
        nodes.add(node10);
        nodes.add(node11);
        nodes.add(node12);
        nodes.add(node13);
        nodes.add(node14);
        nodes.add(node15);
        nodes.add(node16);
        nodes.add(nodeEnd);


        // Tạo các đối tượng Edge và thêm vào danh sách các cạnh
//        1 =[2-250;15-76;10-150]
        edges.add(new Edge(node1, node2, 250));
        edges.add(new Edge(node1, node10, 150));
        edges.add(new Edge(node1, node16, 76));
        //2 =[3-100]
        edges.add(new Edge(node2, node3, 101));
        edges.add(new Edge(node2, node1, 251));
        //3 =[4-31]
        edges.add(new Edge(node3, node4, 31));
        edges.add(new Edge(node3, node2, 102));
        //4 =[5-34]
        edges.add(new Edge(node4, node5, 34));
        edges.add(new Edge(node4, node3, 32));
        //5 =[6-47]
        edges.add(new Edge(node5, node6, 47));
        edges.add(new Edge(node5, node4, 35));
        //6 =[7-65;8-52]
        edges.add(new Edge(node6, node7, 64));
        edges.add(new Edge(node6, node8, 52));
        edges.add(new Edge(node6, node5, 48));
        //7 =[9-54]
        edges.add(new Edge(node7, node9, 54));
        edges.add(new Edge(node7, node6, 65));
        //8 =[9-65;10-120]
        edges.add(new Edge(node8, node9, 66));
        edges.add(new Edge(node8, node10, 121));
        edges.add(new Edge(node8, node6, 53));
        //9 =[11-120]
        edges.add(new Edge(node9, node11, 120));
        edges.add(new Edge(node9, node8, 67));
        edges.add(new Edge(node9, node7, 55));
        //10 =[11-70;13-130]
        edges.add(new Edge(node10, node11, 70));
        edges.add(new Edge(node10, node13, 130));
        edges.add(new Edge(node10, node8, 122));
        edges.add(new Edge(node10, node1, 151));
        //11 =[12-120]
        edges.add(new Edge(node11, node12, 123));
        edges.add(new Edge(node11, node9, 125));
        edges.add(new Edge(node11, node10, 71));
        //12 =[13-78]
        edges.add(new Edge(node12, node13, 78));
        edges.add(new Edge(node12, node11, 124));
        //13 =[14-100]
        edges.add(new Edge(node13, node14, 103));
        edges.add(new Edge(node13, node12, 79));
        edges.add(new Edge(node13, node10, 131));
        //14 =[15-75]
        edges.add(new Edge(node14, node15, 62));
        edges.add(new Edge(node14, node13, 104));
        //15 =[]
        edges.add(new Edge(node15, node16, 17));
        edges.add(new Edge(node15, node14, 63));
        //16
        edges.add(new Edge(node16, node15, 18));
        edges.add(new Edge(node16, node1, 77));
        /// node cho start
        close_distance = Float.MAX_VALUE;
        close_distance2 = Float.MAX_VALUE;
        close_node = 100;
        close_node2 = 100;
        for (int i = 1; i < (nodes.size()-1); i++){
            float dis = distance(nodes.get(0).getLocation().latitude,nodes.get(0).getLocation().longitude,
                    nodes.get(i).getLocation().latitude,nodes.get(i).getLocation().longitude);
            if (close_distance>dis){
                close_distance = dis;
                close_node = i;
                System.out.println("start   " + close_distance + "///" + close_node);
            }
        }
        for (int i = 1; i < (nodes.size()-1); i++){
            if (close_node!= 100){
                float angle = goc_co_huong(nodes.get(close_node).getLocation().latitude, nodes.get(close_node).getLocation().longitude,
                        nodeStart.getLocation().latitude,nodeStart.getLocation().longitude,
                        nodes.get(i).getLocation().latitude,nodes.get(i).getLocation().longitude);
                if (angle<20 && angle>-20){
                    float dis = distance(nodeStart.getLocation().latitude,nodeStart.getLocation().longitude,
                            nodes.get(i).getLocation().latitude,nodes.get(i).getLocation().longitude);
                    if (close_distance2>dis && close_node!=i){
                        close_distance2=dis;
                        close_node2 = i;
                        System.out.println("start2   "  + close_distance2 + "///" + close_node2);
                    }
                }
            }
        }
        edges.add(new Edge(nodeStart, nodes.get(close_node), close_distance));
        if (close_node2!=100){
            edges.add(new Edge(nodeStart, nodes.get(close_node2), close_distance2));
        }
//        /// node cho end
        close_distance3 = Float.MAX_VALUE;
        close_distance4 = Float.MAX_VALUE;
        close_node4 = 100;
        close_node3 = 100;

        for (int i = 0; i < (nodes.size()-1); i++){
            float dis = distance(nodes.get(nodes.size()-1).getLocation().latitude,nodes.get(nodes.size()-1).getLocation().longitude,
                    nodes.get(i).getLocation().latitude,nodes.get(i).getLocation().longitude);
            if (close_distance3>dis){
                close_distance3 = dis;
                close_node3 = i;
                System.out.println("end3   " + close_distance3 + "///" + close_node3);
            }
        }
        for (int i = 0; i < (nodes.size()-1); i++){
            if (close_node3!= 100){
                float angle = goc_co_huong(nodes.get(close_node3).getLocation().latitude, nodes.get(close_node3).getLocation().longitude,
                        nodeEnd.getLocation().latitude,nodeEnd.getLocation().longitude,
                        nodes.get(i).getLocation().latitude,nodes.get(i).getLocation().longitude);
                if (angle<20 && angle>-20){
                    float dis = distance(nodeEnd.getLocation().latitude,nodeEnd.getLocation().longitude,
                            nodes.get(i).getLocation().latitude,nodes.get(i).getLocation().longitude);
                    if (close_distance4>dis && close_node3!=i){
                        close_distance4=dis;
                        close_node4 = i;
                        System.out.println("end4   "  + close_distance4 + "///" + close_node4);
                    }
                }
            }
        }
        edges.add(new Edge(nodes.get(close_node3), nodeEnd, close_distance3));
        if (close_node4!=100){
            edges.add(new Edge(nodes.get(close_node4), nodeEnd, close_distance4));
        }
        // Tạo đối tượng Graph từ danh sách các đỉnh và các cạnh
        graph = new Graph(nodes, edges);
        if (close_node2!=100){
            graph.removeEdges(nodes.get(close_node),nodes.get(close_node2));
        }
        if (close_node4!=100){
            graph.removeEdges(nodes.get(close_node3),nodes.get(close_node4));
        }
//        if (cut_save==0 && cutnode>0){
//            cut_save = cutnode;
//        }
        if (cutnode>0){
            if (!cut_node_save.contains(cutnode)) {
                cut_node_save.add(cutnode);
                if (close_node==cutnode && close_node2<100 && !path_cut_save.contains(close_node2)){
                    path_cut_save.add(close_node2);
                }
                if (close_node2==cutnode && close_node<100 && !path_cut_save.contains(close_node)){
                    path_cut_save.add(close_node);
                }
            }
        }
        if (change==1 && check_change==1){
            if (rev==1){
                System.out.println("cutnode" + cut_node_save + change);
                System.out.println("cutnode path " + path_cut_save);
                for (int i = 0; i < (cut_node_save.size()); i++){
                    if (cut_node_save.get(i)==18){
                        graph.removeEdges(nodes.get(17), nodeEnd);
                    }else {
                        graph.removeEdges(nodes.get(cut_node_save.get(i)), nodeEnd);
                    }
                    if (path_cut_save.size()>0){
                        if (i<path_cut_save.size()){
                            graph.removeEdges(nodes.get(cut_node_save.get(i)), nodes.get(path_cut_save.get(i)));
                        }
                    }
                }
            }else{
                System.out.println("cutnode" + cut_node_save + change);
                System.out.println("cutnode path " + path_cut_save);
                for (int i = 0; i < (cut_node_save.size()); i++){
                    if (cut_node_save.get(i)==18){
                        graph.removeEdges(nodeStart,nodes.get(17));
                    }else {
                        graph.removeEdges(nodeStart,nodes.get(cut_node_save.get(i)));
                    }
                    if (path_cut_save.size()>0){
                        if (path_cut_save.size()<cut_node_save.size()){
                            path_cut_save.add(path_cut_save.get(path_cut_save.size()-1));
                        }else{
                            if (i<path_cut_save.size()){
                                if (cut_node_save.get(i)==18){
                                    graph.removeEdges(nodes.get(17), nodes.get(path_cut_save.get(i)));
                                }else {
                                    graph.removeEdges(nodes.get(cut_node_save.get(i)), nodes.get(path_cut_save.get(i)));
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Tổng  "  + close_distance + "///" + close_distance2 + "///" + close_distance3 + "///" + close_distance4 + "///");
        System.out.println("Tổng  "  + close_node + "///" + close_node2 + "///" + close_node3 + "///" + close_node4 + "///");
        // Đặt đỉnh bắt đầu và đỉnh kết thúc
        startNode = nodeStart;
        endNode = nodeEnd;
    }
    private double checkdistance(int rev) {
        // Thực hiện thuật toán Dijkstra để tìm đường đi
        Dijkstra dijkstra = new Dijkstra(graph);
        dijkstra.execute(startNode);
        List<Node> path = dijkstra.getPath(endNode);
        System.out.println("check" + path + rev);
        double totalDistance = 1;
        if (path.size()>2){
            totalDistance = dijkstra.getTotalDistance(endNode);
        }
        return totalDistance;
}
    private int checkpath(int rev) {
        // Thực hiện thuật toán Dijkstra để tìm đường đi
        Dijkstra dijkstra = new Dijkstra(graph);
        dijkstra.execute(startNode);
        List<Node> path = dijkstra.getPath(endNode);
        if (path==null){
            return 0;
        }

        if (path.size()>3){
            if (rev==0){
                System.out.println("check alo" + path.size() + " /// " + path.get(2).getIndex());
                return path.size() + path.get(2).getIndex();
            }else{
                System.out.println("check alo" + path.size() + " /// " + path.get(path.size()-2).getIndex());
                return path.size() + path.get(path.size()-2).getIndex();
            }
        }else{
            return 1;
        }
    }
    private void findPath(int change, int rev) {
        // Thực hiện thuật toán Dijkstra để tìm đường đi
        Dijkstra dijkstra = new Dijkstra(graph);
        dijkstra.execute(startNode);
        List<Node> path = dijkstra.getPath(endNode);
        System.out.println(path);
        if (path.size()>2){
            double totalDistance = dijkstra.getTotalDistance(endNode);
            System.out.println("total dis"+totalDistance);
        }
        if (rev==1){
            for (int i = 1, j = path.size() - 1; i < j; i++, j--) {
                Node temp = path.get(i);
                path.set(i, path.get(j));
                path.set(j, temp);
            }
        }
        instruction_list = new ArrayList<>();
        if (path2.size()<2 || lock_check==0){
            path2 = path;
//            if (path2.get(1).getIndex()==18){
//                Collections.swap(path2, 1, path2.size() - 1);
//            }
        }
        if (path.size()>1){
            path2.set(1,path.get(1));
        }
        if (path2.size()>2){
            if (path2.size()>3 && check2==1){
                // đảo ngược
                float angle = goc_co_huong(path2.get(path2.size()-2).getLocation().latitude, path2.get(path2.size()-2).getLocation().longitude,
                        path2.get(path2.size()-1).getLocation().latitude,path2.get(path2.size()-1).getLocation().longitude,
                        path2.get(path2.size()-3).getLocation().latitude,path2.get(path2.size()-3).getLocation().longitude);
                System.out.println("angle" + angle);
                if (angle<20 && angle>-20){
                    System.out.println("remove");
                    path2.remove(path2.size()-2);
                }
            }
            if (path2.size()>3 && check2==0){
                float angle = goc_co_huong(path2.get(2).getLocation().latitude, path2.get(2).getLocation().longitude,
                        path2.get(1).getLocation().latitude,path2.get(1).getLocation().longitude,
                        path2.get(3).getLocation().latitude,path2.get(3).getLocation().longitude);
                System.out.println("angle" + angle);
                if (angle<25 && angle>-25){
                    System.out.println("remove");
                    path2.remove(2);
                }
            }
            for (int i = 0; i < path2.size(); i++) {
                if (path2.get(i)!=null){
                    instruction_list.add(path2.get(i).getIndex());
                }
            }
            map.addPolyline(new PolylineOptions()
                    .add(path2.get(1).getLocation(), path2.get(2).getLocation())
                    .width(5)
                    .color(Color.RED));
            for (int i = 2; i < path2.size(); i++) {
                System.out.println("after"+path2);
                if (path2.get(i)!=null){
//                System.out.println("index" + path.get(i).getIndex());
                    LatLng startLatLng = path2.get(i).getLocation();
                    if (i<path2.size()-1){
                        LatLng endLatLng = path2.get(i + 1).getLocation();
                        map.addPolyline(new PolylineOptions()
                                .add(startLatLng, endLatLng)
                                .width(5)
                                .color(Color.RED));
                    }
                }
            }
        }
        System.out.println("alo" + instruction_list);
    }
    private float distance(double lat_start, double lng_start, double lat_end, double lng_end){
        float distance = 0;
        float[] results = new float[1];
        Location.distanceBetween(
                lat_start, lng_start,
                lat_end, lng_end,
                results);
        distance = results[0];
        return distance;
    }
    private void addMarker(double lat, double lng){
        LatLng latLng = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        map.addMarker(markerOptions);
    }
    private float goc_co_huong(double lat_A, double lng_A, double lat_B, double lng_B, double lat_C, double lng_C){
        LatLng pointA = new LatLng(lat_A, lng_A);
        LatLng pointB = new LatLng(lat_B, lng_B);
        LatLng pointC = new LatLng(lat_C, lng_C);
        int a = 0;
        float angleAB = Utils.calculateHeading(pointA, pointB);
        float angleBC = Utils.calculateHeading(pointB, pointC);

        float angleDiff = angleAB - angleBC;
        if (angleDiff > 180.0) {
            angleDiff -= 360.0;
        } else if (angleDiff < -180.0) {
            angleDiff += 360.0;
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(pointA, 18));
        return angleDiff;
    }
    private int signal(double lat_A, double lng_A, double lat_B, double lng_B, double lat_C, double lng_C){
        LatLng pointA = new LatLng(lat_A, lng_A);
        LatLng pointB = new LatLng(lat_B, lng_B);
        LatLng pointC = new LatLng(lat_C, lng_C);
        int a = 0;
        float angleAB = Utils.calculateHeading(pointA, pointB);
        float angleBC = Utils.calculateHeading(pointB, pointC);

        float angleDiff = angleAB - angleBC;
        if (angleDiff > 180.0) {
            angleDiff -= 360.0;
        } else if (angleDiff < -180.0) {
            angleDiff += 360.0;
        }

        String turnSignal;
        if (angleDiff > 60) {
            turnSignal = "Turn left";
            a = 1;
        } else if (angleDiff < -60) {
            turnSignal = "Turn right";
            a = 2;
        } else {
            turnSignal = "Go straight";
            a = 0;
        }

        System.out.println( "Turn signal: " + turnSignal + "/// "+angleDiff);
        return a;
    }
    private double goc_vo_huong(double lat_A, double lng_A, double lat_B, double lng_B, double lat_C, double lng_C){
        Location locA = new Location("");
        locA.setLatitude(lat_A);
        locA.setLongitude(lng_A);

        Location locB = new Location("");
        locB.setLatitude(lat_B);
        locB.setLongitude(lng_B);

        Location locC = new Location("");
        locC.setLatitude(lat_C);
        locC.setLongitude(lng_C);

        float distanceAB = locA.distanceTo(locB);
        float bearingAB = locA.bearingTo(locB);

        float distanceBC = locB.distanceTo(locC);
        float bearingBC = locB.bearingTo(locC);

        // Tính góc giữa hai cặp điểm liên tiếp
        float angle = bearingBC - bearingAB;
        if (angle > 360) {
            angle -= 360;
        } else if (angle < 0) {
            angle += 360;
        }
        if (angle > 180) {
            angle = 360 - angle;
        } else if (angle < 90) {
            angle = 180 - angle;
        }

            // In kết quả
            return angle;
    }
    private void checkpermission() {
        Dexter.withContext(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        isPermissionsGranted = true;
                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri=Uri.fromParts("package", getPackageName(),"");
                        intent.setData(uri);
                        startActivity(intent);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    private boolean checkggService() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (result == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleApiAvailability.isUserResolvableError(result)) {
            Dialog dialog = googleApiAvailability.getErrorDialog(this, result, 201, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Toast.makeText(MainActivity.this, "user cancel dialog", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        }
        return false;
    }
    private void checkGPS() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(500);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);
        Task<LocationSettingsResponse> locationSettingsResponseTask = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());
        locationSettingsResponseTask.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse reponse = task.getResult(ApiException.class);
                    get_Current_Location_Update();
                    Toast.makeText(MainActivity.this, "GPS is enabled", Toast.LENGTH_SHORT).show();
                } catch (ApiException e) {
                    if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        try {
                            resolvableApiException.startResolutionForResult(MainActivity.this, 101);
                        } catch (IntentSender.SendIntentException sendIntentException) {
                            sendIntentException.printStackTrace();
                        }

                    }
                    if (e.getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                        Toast.makeText(MainActivity.this, "Setting not available", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
    private void get_Current_Location_Update() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                myRef.child("/my_location/lat").setValue(locationResult.getLastLocation().getLatitude());
                lat_location = locationResult.getLastLocation().getLatitude();
                myRef.child("/my_location/lng").setValue(locationResult.getLastLocation().getLongitude());
                lng_location = locationResult.getLastLocation().getLongitude();
            }
        }, Looper.getMainLooper());
    }
    private void XacnhanxoaMaker(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Note");
//        alertDialog.setIcon(R.drawable.alert);
        alertDialog.setMessage("Delete all marker?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "Deleted all marker", Toast.LENGTH_SHORT).show();
                map.clear();
                path2 = new ArrayList<>();
                instruction_list = new ArrayList<>();
                cut_node_save = new ArrayList<>();
                path_cut_save = new ArrayList<>();
                graph2 = null;
                myRef.child("/change").setValue(0);
                cut_save=0;
                change_road=0;
                cut=0;
                lock_check=0;
                lock.setChecked(false);
                check_change=0;

            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alertDialog.show();
    }
    private void doiduong(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Note");
        alertDialog.setMessage("Change Route?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "Changing route ...", Toast.LENGTH_SHORT).show();
                map.clear();
                path2 = new ArrayList<>();
                instruction_list = new ArrayList<>();
                graph2 = null;
                change.setChecked(false);
                check_circle=0;
                check_change=1;
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                myRef.child("/change").setValue(0);
                check_change=0;
            }
        });
        alertDialog.show();
    }
    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
        lat_des = latLng.latitude;
        lng_des = latLng.longitude;
        myRef.child("/set_point/lat").setValue(latLng.latitude);
        myRef.child("/set_point/lng").setValue(latLng.longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        map.addMarker(markerOptions);
    }
    private void addCircle(LatLng latLng, float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.fillColor(Color.argb(60,0,0,255));
        circleOptions.strokeColor(Color.argb(150,0,0,255));
        circleOptions.strokeWidth(3);
        map.addCircle(circleOptions);
    }
    @Override
    protected void onResume() {

        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, delay);
                GenerateRandom();
            }
        }, delay);

        super.onResume();
    }
    private void GenerateRandom() {
        check_waypoint = check_waypoint + 1;
        if (check_waypoint>50){
            check_waypoint = 0;
        }
//        float signal = goc_co_huong(10.851422, 106.771340,lat_location, lng_location,10.853683, 106.771619);
        myRef.child("/test").setValue(check_waypoint);
        if (change_road==1){
            if (instruction_list!=null){
                if (instruction_list.size()>1 && lock_check==0 && cut==0){
                    cut = instruction_list.get(1);
                    System.out.println("cutnode"+cut);
//                    path2 = new ArrayList<>();
//                    instruction_list = new ArrayList<>();
                }
                if (lock_check==1){
                    change_road=0;
                    cut = 0;
                    myRef.child("/change").setValue(0);
                    check_change=0;
                }
            }
        }
        if (check==1){
            if (check2==1){
                reverse=1;
                createGraph(lat_des, lng_des,lat_location, lng_location, change_road, cut, reverse);
                findPath(change_road, reverse);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat_location,lng_location), 18));
                if (instruction_list!=null){
                    if (instruction_list.size()>1){
                        check_circle =1;
                        for (int i = 1; i < (instruction_list.size()-1); i++){
                            addCircle(path2.get(i+1).getLocation(),17);
                        }
                        addCircle(path2.get(instruction_list.size()).getLocation(),12);
                    }
                }
            }
            else{
                System.out.println("alo" + lock_check);

                reverse=0;
                createGraph(lat_location, lng_location,lat_des, lng_des, change_road, cut, reverse);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat_location,lng_location), 18));
                findPath(change_road, reverse);
                if (instruction_list!=null){
                    if (instruction_list.size()>1){
                        check_circle =1;
                        for (int i = 1; i < (instruction_list.size()-1); i++){
                            addCircle(path2.get(i+1).getLocation(),17);
                        }
                        addCircle(path2.get(instruction_list.size()).getLocation(),12);
                    }
                }
            }
        }
        if (instruction_list!=null && instruction_list.size()>2){
            for (int i = 1; i < (instruction_list.size()-1); i++){
                float distance_check = distance(lat_location, lng_location,
                        path2.get(2).getLocation().latitude, path2.get(2).getLocation().longitude);
                System.out.println("distance: " + distance_check);
                if (instruction_list.size()>2){
                    int signal = signal(path2.get(1).getLocation().latitude, path2.get(1).getLocation().longitude,
                            path2.get(2).getLocation().latitude,path2.get(2).getLocation().longitude,
                            path2.get(3).getLocation().latitude,path2.get(3).getLocation().longitude);
                    if (distance_check<18){
                        if (signal==1){
                            myRef.child("/warning_python").setValue("" + "Turn Left");
                            Toast.makeText(this, "Turn Left", Toast.LENGTH_SHORT).show();
                        }else if (signal==2){
                            myRef.child("/warning_python").setValue("" + "Turn Right");
                            Toast.makeText(this, "Turn Right", Toast.LENGTH_SHORT).show();
                        }else {
                            myRef.child("/warning_python").setValue("" + "Keep Straight");
                            Toast.makeText(this, "Keep Straight", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        myRef.child("/warning_python").setValue("" + "Keep Straight");
                        Toast.makeText(this, "Keep Straight", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }else if(instruction_list!=null && instruction_list.size()==2) {
            float distance_check = distance(lat_location, lng_location,
                    path2.get(2).getLocation().latitude, path2.get(2).getLocation().longitude);
            System.out.println("alo alo" + distance_check);
            if(distance_check<13){
                check = 0;
                update.setChecked(false);
                myRef.child("/warning_python").setValue("Finish");
                finish_alert();
            }
        }
    }
    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }
    private void finish_alert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Note");
        alertDialog.setMessage("you have reached your destination");
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                map.clear();
                path2 = new ArrayList<>();
                instruction_list = new ArrayList<>();
                graph2 = null;
                myRef.child("/change").setValue(0);
                cut_save=0;
            }
        });
        alertDialog.show();
    }
}