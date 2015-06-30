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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import static com.google.appengine.api.datastore.Query.FilterOperator;
import static com.google.appengine.api.datastore.Query.FilterPredicate;

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
            registrationEntity.setProperty("Mobile_Number", registrationBean.getMobile_Number());
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
        //dummyPostRequest();
    }

    //ApiMethod to store Group name details on server.....
    @ApiMethod(name = "storeGroup")
    public void storeGroup(GroupBean groupBean) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        //Transaction txn = datastoreService.beginTransaction();
        try {
            Key taskBeanParentKey = KeyFactory.createKey("Details", "Group");
            Entity groupEntity = new Entity("User Group Details", taskBeanParentKey);
            groupEntity.setProperty("Group Name", groupBean.getGroup_Name());
            groupEntity.setProperty("Group Member", groupBean.getGroup_Member());
            groupEntity.setProperty("Mobile Number", groupBean.getMobile_Number());
            groupEntity.setProperty("deviceId", groupBean.getDevice_Id());
            groupEntity.setProperty("CompositeGroupKey", groupBean.getCompositeGroupKey());
            datastoreService.put(groupEntity);

            //creating group member details entity....
            for (int i = 0; i < groupBean.getGroup_Member().size(); i++) {
                String isAdmin = "false";
                if (groupBean.getMobile_Number().equals(groupBean.getGroup_Member().get(i))) {
                    isAdmin = "true";
                }
                Key newtaskBeanParentKey = KeyFactory.createKey("Group Member Details", "Group Member");
                Entity groupMemberEntity = new Entity("User Group Member Details", newtaskBeanParentKey);
                groupMemberEntity.setProperty("MobileNumber_Member", groupBean.getGroup_Member().get(i));
                groupMemberEntity.setProperty("MobileNumber_Admin", groupBean.getMobile_Number());
                groupMemberEntity.setProperty("Group Name", groupBean.getGroup_Name());
                groupMemberEntity.setProperty("compositeGroupKey", groupBean.getCompositeGroupKey());
                groupMemberEntity.setProperty("isAccepted", isAdmin);
                datastoreService.put(groupMemberEntity);
            }



            dummyPostRequest(groupBean.getGroup_Member());
            //txn.commit();
        }/* finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }*/ catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //this method is used for add more members into existing group.........
    @ApiMethod(name = "addAnotherGroupMember")
    public void addAnotherGroupMember(GroupBean groupBean) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        try {
            Key addAnotherGroupMemberKey = KeyFactory.createKey("Details", "Group");
            Query addAnotherGroupMemberQuery = new Query("User Group Details", addAnotherGroupMemberKey);
            FilterPredicate filterUsingGroupNameIntoUserGroupDetails = new FilterPredicate("Group Name", FilterOperator.EQUAL, groupBean.getGroup_Name());
            FilterPredicate filterUsingGroupAdminNuber = new FilterPredicate("Mobile Number", FilterOperator.EQUAL, groupBean.getMobile_Number());
            Query.CompositeFilter addMemberFilterjoin = Query.CompositeFilterOperator.and(filterUsingGroupNameIntoUserGroupDetails, filterUsingGroupAdminNuber);
            addAnotherGroupMemberQuery.setFilter(addMemberFilterjoin);
            PreparedQuery preparedQuery = datastoreService.prepare(addAnotherGroupMemberQuery);
            for (Entity result : preparedQuery.asIterable()) {
                ArrayList<String> preiviousList = new ArrayList<>();
                preiviousList = (ArrayList<String>) result.getProperty("Group Member");
                for (int i = 0; i < groupBean.getGroup_Member().size(); i++) {
                    preiviousList.add(groupBean.getGroup_Member().get(i));
                    //save new data accordingly into another entity...........

                    String isAdmin = "false";
                    if (groupBean.getMobile_Number().equals(groupBean.getGroup_Member().get(i))) {
                        isAdmin = "true";
                    }
                    Key newtaskBeanParentKey = KeyFactory.createKey("Group Member Details", "Group Member");
                    Entity groupMemberEntity = new Entity("User Group Member Details", newtaskBeanParentKey);
                    groupMemberEntity.setProperty("MobileNumber_Member", groupBean.getGroup_Member().get(i));
                    groupMemberEntity.setProperty("MobileNumber_Admin", groupBean.getMobile_Number());
                    groupMemberEntity.setProperty("Group Name", groupBean.getGroup_Name());
                    groupMemberEntity.setProperty("compositeGroupKey", result.getProperty("CompositeGroupKey"));
                    groupMemberEntity.setProperty("isAccepted", isAdmin);
                    datastoreService.put(groupMemberEntity);
                }
                result.setProperty("Group Member", preiviousList);
                datastoreService.put(result);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    //ApiMethod to get Registration using key....
    @ApiMethod(name = "getRegistrationDetailUsingKey")
    public RegistrationBean getRegistrationDetailUsingKey(@Named("id") String id) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key keyId = KeyFactory.createKey("Details", "Registration");
        Key getDetailKey = KeyFactory.createKey(keyId, "User Registration Data", id);
        RegistrationBean registration = new RegistrationBean();
        try {
            Entity detailsEntity = datastoreService.get(getDetailKey);
            registration.setDevice_Id(detailsEntity.getKey().getName());
            registration.setMobile_Number((String) detailsEntity.getProperty("Mobile_Number"));
            registration.setEmail_Id((String) detailsEntity.getProperty("Email_ID"));
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        }
        return registration;
    }


    //Group details fetch based on group id
    /*@ApiMethod(name = "getGRoupDetailUsingKey")
    public ArrayList<GroupBean> getGRoupDetailUsingKey(@Named("id") String id) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key registrationBeanParentKey = KeyFactory.createKey("Details", "Group");
        Query q = new Query("User Group Details", registrationBeanParentKey);
        FilterPredicate propertyFilter = new FilterPredicate("deviceId", FilterOperator.EQUAL, id);
        q.setFilter(propertyFilter);
        //Use PreparedQuery interface to retrieve results
        ArrayList<GroupBean> groupBeans = new ArrayList<>();
        PreparedQuery pq = datastoreService.prepare(q);
        for (Entity result : pq.asIterable()) {
            GroupBean groupDetails = new GroupBean();
            //contact.setMobileNumber((String) result.getProperty("Mobile_Number"));
            groupDetails.setGroup_Name((String) result.getProperty("Group_Name"));
            groupDetails.setGroup_Member((ArrayList<String>) result.getProperty("Group_Member"));
            groupBeans.add(groupDetails);
        }
        return groupBeans;
    }*/

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
            location.setDrvice_Id(re.getKey().getName());
            locationBean.add(location);
        }
        return locationBean;
    }

    //ApiMethod to delete registration details....
    @ApiMethod(name = "forgetMe")
    public void forgetMe(@Named("id") String id) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        try {
            Key taskBeanParentKey = KeyFactory.createKey("Details", "Registration");
            Key taskResult = KeyFactory.createKey(taskBeanParentKey, "User Registration Data", id);
            datastoreService.delete(taskResult);
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }


    //this apiMethod is used for sync all contacts from server who is registered......
    @ApiMethod(name = "contactSync")
    public ContactSyncBean contactSync(ContactSyncBean contacts) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key registrationBeanParentKey = KeyFactory.createKey("Details", "Registration");
        Query q = new Query("User Registration Data", registrationBeanParentKey);
        ContactSyncBean contactSyncBean = new ContactSyncBean();
        ArrayList<String> returnNumber = new ArrayList<>();
        ArrayList<String> returnName = new ArrayList<>();
        ArrayList<String> contactSyncBeansNumber = contacts.getNumber();

        ArrayList<String> contactSyncBeansName = contacts.getName();
        for (int i = 0; i < contactSyncBeansNumber.size(); i++) {
            String contact_Number = contactSyncBeansNumber.get(i);
            String contact_Name = contactSyncBeansName.get(i);
            FilterPredicate propertyFilter = new FilterPredicate("Mobile_Number", FilterOperator.EQUAL, contact_Number);
            q.setFilter(propertyFilter);
            //Use PreparedQuery interface to retrieve results
            PreparedQuery pq = datastoreService.prepare(q);
            for (Entity result : pq.asIterable()) {
                returnNumber.add((String) result.getProperty("Mobile_Number"));
                returnName.add(contact_Name);
            }
        }
        contactSyncBean.setNumber(returnNumber);
        contactSyncBean.setName(returnName);
        return contactSyncBean;
    }

    //this apiMethod is used for sync groupMembers in a group.....
    @ApiMethod(name = "groupMemberSync")
    public ArrayList<GroupMemberSyncBean> groupMemberSync(@Named("mobileNumber") String mobileNumber) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key groupMemberBeanParentKey = KeyFactory.createKey("Group Member Details", "Group Member");
        Query queryGroupMemberDetails = new Query("User Group Member Details", groupMemberBeanParentKey);
        Key groupBeanParentKey = KeyFactory.createKey("Details", "Group");
        Query queryGroupDetails = new Query("User Group Details", groupBeanParentKey);
        //create object of GroupMemberSyncBean class....
        ArrayList<GroupMemberSyncBean> groupMemberList = new ArrayList<>();

        FilterPredicate propertyFilter = new FilterPredicate("MobileNumber_Member", FilterOperator.EQUAL, mobileNumber);
        queryGroupMemberDetails.setFilter(propertyFilter);
        ArrayList<String> returnGroupAdminNumber = new ArrayList<>();
        ArrayList<String> isAccepted = new ArrayList<>();
        ArrayList<String> returnGroupName = new ArrayList<>();
        ArrayList<String> returnCompositeGroupKey = new ArrayList<>();
        //Use PreparedQuery interface to retrieve results
        PreparedQuery pq = datastoreService.prepare(queryGroupMemberDetails);
        for (Entity result : pq.asIterable()) {
            returnGroupAdminNumber.add((String) result.getProperty("MobileNumber_Admin"));
            returnGroupName.add((String) result.getProperty("Group Name"));
            returnCompositeGroupKey.add((String) result.getProperty("compositeGroupKey"));
            isAccepted.add((String) result.getProperty("isAccepted"));

        }
        int count = 0;
        for (int i = 0; i < returnGroupAdminNumber.size(); i++) {
            FilterPredicate groupPropertyFilterCompositeKey = new FilterPredicate("CompositeGroupKey", FilterOperator.EQUAL, returnCompositeGroupKey.get(i));
            //FilterPredicate groupPropertyFilterNumber = new FilterPredicate("Mobile Number", FilterOperator.EQUAL, returnGroupAdminNumber.get(i));
            //FilterPredicate groupPropertyFilterNumber = new FilterPredicate("Mobile Number", FilterOperator.EQUAL, "+918004517260");
            //FilterPredicate groupPropertyFilterName = new FilterPredicate("Group Name", FilterOperator.EQUAL, "again new group");
            //Query.CompositeFilter groupPropertyFilterjoin  =  Query.CompositeFilterOperator.and(groupPropertyFilterNumber, groupPropertyFilterName);
            queryGroupDetails.setFilter(groupPropertyFilterCompositeKey);
            //Use PreparedQuery interface to retrieve results
            PreparedQuery preparedQuery = datastoreService.prepare(queryGroupDetails);
            for (Entity result : preparedQuery.asIterable()) {
                count++;
                GroupMemberSyncBean groupMemberSyncBean = new GroupMemberSyncBean();
                //groupMemberSyncBean.setGroupName(returnGroupName.get(i)+"count value is "+returnGroupAdminNumber.size());
                //groupMemberSyncBean.setGroupAdminNumber(returnGroupAdminNumber.get(i));
                //groupMemberSyncBean.setGroupMember((ArrayList<String>) result.getProperty("Group Member"));
                groupMemberSyncBean.setCompositeGroupKey((String) result.getProperty("CompositeGroupKey"));
                groupMemberSyncBean.setGroupName((String) result.getProperty("Group Name"));
                groupMemberSyncBean.setGroupAdminNumber(returnGroupAdminNumber.get(i));
                groupMemberSyncBean.setGroupMember((ArrayList<String>) result.getProperty("Group Member"));
                groupMemberSyncBean.setIsAccepted(isAccepted.get(i));
                groupMemberList.add(groupMemberSyncBean);
            }
        }
        return groupMemberList;
    }

    //this apiMethod is used for sync Acceptance Status of groups......
    @ApiMethod(name = "processAcceptanceStatusRequest")
    public ArrayList<AcceptanceStatusBean> processAcceptanceStatusRequest(AcceptanceStatusBean acceptanceStatusBean) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Key acceptanceBeanKey = KeyFactory.createKey("Group Member Details", "Group Member");
        Query acceptanceStatusQuery = new Query("User Group Member Details", acceptanceBeanKey);

        ArrayList<AcceptanceStatusBean> acceptanceStatusNumberList = new ArrayList<>();

        ArrayList<String> groupNameFromMobile = acceptanceStatusBean.getGroupNameList();
        ArrayList<String> groupMemberNumberFromMobile = acceptanceStatusBean.getGroupMemberNumberList();
        //ArrayList<String> groupNameFromMobile = new ArrayList<>();
        //groupNameFromMobile = groupNameList;
        //ArrayList<String> groupMemberNumberFromMobile = new ArrayList<>();
        //groupMemberNumberFromMobile = groupMemberMobileNumberList;
        for (int i = 0; i < groupMemberNumberFromMobile.size(); i++) {
            String groupName = groupNameFromMobile.get(i);
            String groupMemberNumber = groupMemberNumberFromMobile.get(i);
            FilterPredicate filterUsingGroupName = new FilterPredicate("Group Name", FilterOperator.EQUAL, groupNameFromMobile.get(i));
            FilterPredicate filterUsingGroupMemberNumber = new FilterPredicate("MobileNumber_Member", FilterOperator.EQUAL, groupMemberNumberFromMobile.get(i));
            Query.CompositeFilter groupAcceptanceFilterjoin = Query.CompositeFilterOperator.and(filterUsingGroupName, filterUsingGroupMemberNumber);
            acceptanceStatusQuery.setFilter(groupAcceptanceFilterjoin);
            //prepared query to get all result from above query.......
            PreparedQuery preparedQuery = datastoreService.prepare(acceptanceStatusQuery);
            for (Entity result : preparedQuery.asIterable()) {
                AcceptanceStatusBean acceptanceBean = new AcceptanceStatusBean();
                acceptanceBean.setIsAccepted((String) result.getProperty("isAccepted"));
                acceptanceBean.setGroupName((String) result.getProperty("Group Name"));
                acceptanceBean.setMobileNumber_Member((String) result.getProperty("MobileNumber_Member"));
                acceptanceStatusNumberList.add(acceptanceBean);
            }
        }
        return acceptanceStatusNumberList;
    }

    //this apiMethod is used for set groupAcceptance status.......
    @ApiMethod(name = "memberAcceptanceStatus")
    public AcceptanceStatusBean memberAcceptanceStatus(AcceptanceStatusBean memberAcceptanceStatus) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastoreService.beginTransaction();
        AcceptanceStatusBean statusBean = new AcceptanceStatusBean();
        try {
            Key taskBeanMemberAcceptanceKey = KeyFactory.createKey("Group Member Details", "Group Member");
            Query memberAcceptanceStatusQuery = new Query("User Group Member Details", taskBeanMemberAcceptanceKey);
            //now filter operation to search existing result from datastore......
            FilterPredicate filterUsingGroupName = new FilterPredicate("Group Name", FilterOperator.EQUAL, memberAcceptanceStatus.getGroupName());
            FilterPredicate filterUsingGroupMemberNumber = new FilterPredicate("MobileNumber_Member", FilterOperator.EQUAL, memberAcceptanceStatus.getMobileNumber_Member());
            //combile these filters using composite filter......
            Query.CompositeFilter groupAcceptanceFilterjoin = Query.CompositeFilterOperator.and(filterUsingGroupName, filterUsingGroupMemberNumber);
            //set this cpmposite filter to query.......
            memberAcceptanceStatusQuery.setFilter(groupAcceptanceFilterjoin);
            //now use preparedQuery to get result from above query.....
            PreparedQuery preparedQuery = datastoreService.prepare(memberAcceptanceStatusQuery);

            for (Entity result : preparedQuery.asIterable()) {
                result.setProperty("isAccepted", memberAcceptanceStatus.getIsAccepted());
                datastoreService.put(result);
                //get object to return the data.....
                statusBean.setIsAccepted((String) result.getProperty("isAccepted"));
                statusBean.setMobileNumber_Member((String) result.getProperty("MobileNumber_Member"));
                statusBean.setGroupName((String) result.getProperty("Group Name"));
            }
            txn.commit();

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
        return statusBean;
    }

    //this method is used for delete member from group.......
    @ApiMethod(name = "forgetEntityFromGroup")
    public AcceptanceStatusBean forgetEntityFromGroup(AcceptanceStatusBean acceptanceStatusBean) {
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        AcceptanceStatusBean bean = new AcceptanceStatusBean();
        //Transaction txn = datastoreService.beginTransaction();
        try {
            Key keyMemberDeleteEntity = KeyFactory.createKey("Group Member Details", "Group Member");
            Query memberAcceptanceStatusQuery = new Query("User Group Member Details", keyMemberDeleteEntity);

            //now filter operation to search existing result from datastore......
            FilterPredicate filterUsingGroupName = new FilterPredicate("Group Name", FilterOperator.EQUAL, acceptanceStatusBean.getGroupName());
            FilterPredicate filterUsingGroupMemberNumber = new FilterPredicate("MobileNumber_Member", FilterOperator.EQUAL, acceptanceStatusBean.getMobileNumber_Member());
            //combile these filters using composite filter......
            Query.CompositeFilter groupAcceptanceFilterjoin = Query.CompositeFilterOperator.and(filterUsingGroupName, filterUsingGroupMemberNumber);
            //set this cpmposite filter to query.......
            memberAcceptanceStatusQuery.setFilter(groupAcceptanceFilterjoin);
            //now use preparedQuery to get result from above query.....
            PreparedQuery preparedQuery = datastoreService.prepare(memberAcceptanceStatusQuery);
            for (Entity result : preparedQuery.asIterable()) {
                //this query is used for getting result using groupName and groupAdminNumber from ("Details","Group") entity.....
                Key keyMemberForgetEntityFromUserGroupDetails = KeyFactory.createKey("Details", "Group");
                Query memberForgetQuery = new Query("User Group Details", keyMemberForgetEntityFromUserGroupDetails);
                FilterPredicate filterUsingGroupNameIntoUserGroupDetails = new FilterPredicate("Group Name", FilterOperator.EQUAL, result.getProperty("Group Name"));
                FilterPredicate filterUsingGroupAdminNuber = new FilterPredicate("Mobile Number", FilterOperator.EQUAL, result.getProperty("MobileNumber_Admin"));
                Query.CompositeFilter groupFilterjoin = Query.CompositeFilterOperator.and(filterUsingGroupNameIntoUserGroupDetails, filterUsingGroupAdminNuber);
                memberForgetQuery.setFilter(groupFilterjoin);
                PreparedQuery newPreparedQuery = datastoreService.prepare(memberForgetQuery);
                for (Entity re : newPreparedQuery.asIterable()) {
                    //sizeTemp = groupMemberList.size();
                    for (int i = 0; i < ((ArrayList<String>) re.getProperty("Group Member")).size(); i++) {
                        if (((ArrayList<String>) re.getProperty("Group Member")).get(i).equals(acceptanceStatusBean.getMobileNumber_Member())) {
                            //groupMemberList.add(((ArrayList<String>) re.getProperty("Group Member")).get(i));
                            ((ArrayList<String>) re.getProperty("Group Member")).remove(i);
                        }
                    }
                    re.setProperty("Group Member", re.getProperty("Group Member"));
                    datastoreService.put(re);
                }
                //Now from here delete user MobileNumber_Member from "User Group Member Details" entity........
                datastoreService.delete(result.getKey());
                //get object to return the data.....
                bean.setIsAccepted((String) result.getProperty("isAccepted"));
                bean.setMobileNumber_Member((String) result.getProperty("MobileNumber_Member"));
                bean.setGroupName((String) result.getProperty("Group Name"));
            }
            //txn.commit();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /*finally {
            if(txn.isActive()){
                txn.rollback();
            }
        }*/
        return bean;
    }

    //this apiMethod is used for POST request to duummy server......
    @ApiMethod(name = "dummyPostRequest")
    public void dummyPostRequest(@Named("group_member") ArrayList<String> group_member) {
        try {
            String message = URLEncoder.encode(String.valueOf(group_member), "UTF-8");
            URL url = new URL("https://qcapp-prateeksonkar.rhcloud.com/register");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write("message=" + message);
            writer.close();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // OK
            } else {
                // Server returned HTTP error code.
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}