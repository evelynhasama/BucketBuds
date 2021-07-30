package com.evelynhasama.bucketbuds;

import android.content.Context;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Assert;
import org.junit.Before;
import org.robolectric.Robolectric;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import java.io.IOException;
import java.util.ArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(RobolectricTestRunner.class)
public class InspoFragmentUnitTest{

    LoginActivity mActivity;
    InspoFragment inspoFragment;
    Context mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    ArrayList<ActivityObj> activityObjs;

    @Before
    public void setUp(){
        mActivity = Robolectric.setupActivity(LoginActivity.class);
        inspoFragment = new InspoFragment();
        startFragment(inspoFragment);
    }

    @Test
    public void testActivity() {
        Assert.assertNotNull(mActivity);
    }

    @Test
    public void testIEventAPIData() throws IOException, InterruptedException {
        activityObjs = new ArrayList<>();
        inspoFragment.mApis = new IEventAPI[]{ApiTestHelper.getInstance()};
        inspoFragment.mAdapter = new InspoActivitiesAdapter(mContext, activityObjs, null);
        inspoFragment.getAPIData(mContext);
        Thread.sleep(5000);
        assertEquals(2, inspoFragment.mAdapter.getItemCount());
        assertEquals(2, activityObjs.size());
        assertEquals("Activity Named 1", activityObjs.get(0).getName());
        assertEquals("description number 1", activityObjs.get(0).getDescription());
        assertEquals("1 Hacker Way", activityObjs.get(1).getLocation());
        assertEquals("facebook.com", activityObjs.get(1).getWeb());
        assertNotEquals(0, activityObjs.size());
    }

    private void startFragment(InspoFragment inspoFragment) {
        FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(inspoFragment, null );
        fragmentTransaction.commit();
    }

}