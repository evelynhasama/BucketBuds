package com.evelynhasama.bucketbuds;

import android.view.Menu;
import android.view.MenuItem;
import java.util.List;

public class MenuHelper {

    public static final int LOGOUT = R.id.logout;
    public static final int CREATE = R.id.miCreate;
    public static final int CALENDAR = R.id.miCalendar;
    public static final int ADD = R.id.miAdd;
    public static final int EDIT = R.id.miEdit;
    public static final int TOOL = R.id.miTool;
    public static final int MAP = R.id.miMap;
    public static final int REQUESTS = R.id.miRequests;

    static MenuItem miLogout;
    static MenuItem miCreate;
    static MenuItem miCalendar;
    static MenuItem miAdd;
    static MenuItem miEdit;
    static MenuItem miTool;
    static MenuItem miMap;
    static MenuItem miRequests;

    public static void onCreateOptionsMenu(Menu menu, List<Integer> visibles) {

        miLogout = menu.findItem(LOGOUT);
        miCreate = menu.findItem(CREATE);
        miCalendar = menu.findItem(CALENDAR);
        miAdd = menu.findItem(ADD);
        miEdit = menu.findItem(EDIT);
        miTool = menu.findItem(TOOL);
        miMap = menu.findItem(MAP);
        miRequests = menu.findItem(REQUESTS);

        miLogout.setVisible(visibles.contains(R.id.logout));
        miCreate.setVisible(visibles.contains(R.id.miCreate));
        miCalendar.setVisible(visibles.contains(R.id.miCalendar));
        miAdd.setVisible(visibles.contains(R.id.miAdd));
        miEdit.setVisible(visibles.contains(R.id.miEdit));
        miTool.setVisible(visibles.contains(R.id.miTool));
        miMap.setVisible(visibles.contains(R.id.miMap));
        miRequests.setVisible(visibles.contains(R.id.miRequests));
    }

    public static void setVisible(Menu menu, int id){
        MenuItem menuItem = menu.findItem(id);
        menuItem.setVisible(true);
    }

    public static void setInvisible(Menu menu, int id){
        MenuItem menuItem = menu.findItem(id);
        menuItem.setVisible(false);
    }

}
