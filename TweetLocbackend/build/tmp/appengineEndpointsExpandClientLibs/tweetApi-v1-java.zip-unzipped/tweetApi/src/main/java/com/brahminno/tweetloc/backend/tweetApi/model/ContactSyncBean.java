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
 * on 2015-06-30 at 14:21:39 UTC 
 * Modify at your own risk.
 */

package com.brahminno.tweetloc.backend.tweetApi.model;

/**
 * Model definition for ContactSyncBean.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the tweetApi. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class ContactSyncBean extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String mobileNumber;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<java.lang.String> name;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<java.lang.String> number;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getMobileNumber() {
    return mobileNumber;
  }

  /**
   * @param mobileNumber mobileNumber or {@code null} for none
   */
  public ContactSyncBean setMobileNumber(java.lang.String mobileNumber) {
    this.mobileNumber = mobileNumber;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.String> getName() {
    return name;
  }

  /**
   * @param name name or {@code null} for none
   */
  public ContactSyncBean setName(java.util.List<java.lang.String> name) {
    this.name = name;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<java.lang.String> getNumber() {
    return number;
  }

  /**
   * @param number number or {@code null} for none
   */
  public ContactSyncBean setNumber(java.util.List<java.lang.String> number) {
    this.number = number;
    return this;
  }

  @Override
  public ContactSyncBean set(String fieldName, Object value) {
    return (ContactSyncBean) super.set(fieldName, value);
  }

  @Override
  public ContactSyncBean clone() {
    return (ContactSyncBean) super.clone();
  }

}
