/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2015-03-26 20:30:19 UTC)
 * on 2015-06-23 at 13:30:09 UTC 
 * Modify at your own risk.
 */

package com.brahminno.tweetloc.backend.tweetApi.model;

/**
 * Model definition for GroupMemberSyncBeanCollection.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the tweetApi. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class GroupMemberSyncBeanCollection extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<GroupMemberSyncBean> items;

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<GroupMemberSyncBean> getItems() {
    return items;
  }

  /**
   * @param items items or {@code null} for none
   */
  public GroupMemberSyncBeanCollection setItems(java.util.List<GroupMemberSyncBean> items) {
    this.items = items;
    return this;
  }

  @Override
  public GroupMemberSyncBeanCollection set(String fieldName, Object value) {
    return (GroupMemberSyncBeanCollection) super.set(fieldName, value);
  }

  @Override
  public GroupMemberSyncBeanCollection clone() {
    return (GroupMemberSyncBeanCollection) super.clone();
  }

}
