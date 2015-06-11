/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.brahminno.tweetloc.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(name = "tweetApi", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.tweetloc.brahminno.com", ownerName = "backend.tweetloc.brahminno.com", packagePath = ""))
public class MyEndpoint {

    //ApiMethod to Store Registration data on cloud server....
    @ApiMethod(name = "storeRegistration")
    public void storeRegistration(RegistrationBean registrationBean) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            Key taskBeanParentKey = KeyFactory.createKey("Details", "Registration");
            Entity registrationEntity = new Entity("User Registration Data", registrationBean.getDevice_Id(), taskBeanParentKey);
            registrationEntity.setProperty("Mobile_Nummber", registrationBean.getMobile_Number());
            registrationEntity.setProperty("Email_Id", registrationBean.getEmail_Id());
            datastoreService.put(registrationEntity);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    //Api Methid to store location details....
    @ApiMethod(name = "storeLocation")
    public void storeLocation(LocationBean locationBean) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            Key taskBeanParentKey = KeyFactory.createKey("Details", "location");
            Entity locationEntity = new Entity("User Location Data", locationBean.getDrvice_Id(), taskBeanParentKey);
            locationEntity.setProperty("Latitude", locationBean.getLatitude());
            locationEntity.setProperty("Longitude", locationBean.getLongitude());
            datastoreService.put(locationEntity);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    //ApiMethod to store Group name details on server.....
    @ApiMethod(name = "storeGroup")
    public void storeGroup(GroupBean groupBean) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            Key taskBeanParentKey = KeyFactory.createKey("Details", "Group");
            Entity groupEntity = new Entity("User Group Details",taskBeanParentKey);
            groupEntity.setProperty("Group Name", groupBean.getGroup_Name());
            groupEntity.setProperty("Group Member", groupBean.getGroup_Member());
            groupEntity.setProperty("Mobile Number", groupBean.getMobile_Number());
            groupEntity.setProperty("deviceId",groupBean.getDevice_Id());

            datastoreService.put(groupEntity);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }
    //ApiMethod to get Registration using key....
    @ApiMethod(name = "getRegistrationDetailUsingKey")
    public RegistrationBean getRegistrationDetailUsingKey(@Named("id") String id) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key keyId = KeyFactory.createKey("Details", "Registration");
        Key getDetailKey = KeyFactory.createKey(keyId,"User Registration Data",id);
        RegistrationBean registration = new RegistrationBean();
        try {
            Entity detailsEntity = datastoreService.get(getDetailKey);
            registration.setDevice_Id(detailsEntity.getKey().getName());
            registration.setMobile_Number((String) detailsEntity.getProperty("Mobile Number"));
            registration.setEmail_Id((String) detailsEntity.getProperty("Email_ID"));
        }
        catch(EntityNotFoundException e){
            e.printStackTrace();
        }
        return registration ;
    }
    /*
    //Group details fetch based on group id

    @ApiMethod(name = "getGRoupDetailUsingKey")
    public GroupBean getGRoupDetailUsingKey(@Named("id") String id) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key keyId = KeyFactory.createKey("Details", "Group");
        Key getDetailKey = KeyFactory.createKey(keyId,"User Group Details",id);
        RegistrationBean registration = new RegistrationBean();
        try {
            Entity detailsEntity = datastoreService.get(getDetailKey);
            registration.setDevice_Id(detailsEntity.getKey().getName());
            registration.setMobile_Number((String) detailsEntity.getProperty("Mobile Number"));
            registration.setEmail_Id((String) detailsEntity.getProperty("Email_ID"));
        }
        catch(EntityNotFoundException e){
            e.printStackTrace();
        }
        return registration ;
    }
    */
    //ApiMethod to get location data from server....
    @ApiMethod(name = "getLocation")
    public List<LocationBean> getLocation() {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key taskBeanParentKey = KeyFactory.createKey("Details", "location");
        Query query = new Query(taskBeanParentKey);
        List<Entity> result = datastoreService.prepare(query).asList(FetchOptions.Builder.withDefaults());

        ArrayList<LocationBean> locationBean = new ArrayList<>();
        for (Entity re : result) {
            LocationBean location = new LocationBean();
            location.setLatitude((Double) re.getProperty("Latitude"));
            location.setLongitude((Double) re.getProperty("Longitude"));
            //tweetLocBean.setDevice_Id((String) re.getProperty("Device_Id"));
            location.setDrvice_Id(re.getKey().getName());
            locationBean.add(location);
        }
        return locationBean;
    }

    //ApiMethod to delete registration details....
    @ApiMethod(name = "forgetMe")
    public void forgetMe(@Named("id") String id){
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            Key taskBeanParentKey = KeyFactory.createKey("Details", "Registration");
            Key taskResult = KeyFactory.createKey(taskBeanParentKey, "User Registration Data", id);
            datastoreService.delete(taskResult);
            txn.commit();
        }
        finally {
            if (txn.isActive()){
                txn.rollback();
            }
        }
    }
}
