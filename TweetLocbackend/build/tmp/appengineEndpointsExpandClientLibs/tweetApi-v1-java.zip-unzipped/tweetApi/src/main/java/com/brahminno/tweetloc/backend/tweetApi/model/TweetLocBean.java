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
 * on 2015-05-30 at 10:55:25 UTC 
 * Modify at your own risk.
 */

package com.brahminno.tweetloc.backend.tweetApi.model;

/**
 * Model definition for TweetLocBean.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the tweetApi. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class TweetLocBean extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("device_Id")
  private java.lang.String deviceId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("email_Id")
  private java.lang.String emailId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Double lat;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Double loong;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key("mobile_Number")
  private java.lang.String mobileNumber;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getDeviceId() {
    return deviceId;
  }

  /**
   * @param deviceId deviceId or {@code null} for none
   */
  public TweetLocBean setDeviceId(java.lang.String deviceId) {
    this.deviceId = deviceId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getEmailId() {
    return emailId;
  }

  /**
   * @param emailId emailId or {@code null} for none
   */
  public TweetLocBean setEmailId(java.lang.String emailId) {
    this.emailId = emailId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Double getLat() {
    return lat;
  }

  /**
   * @param lat lat or {@code null} for none
   */
  public TweetLocBean setLat(java.lang.Double lat) {
    this.lat = lat;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Double getLoong() {
    return loong;
  }

  /**
   * @param loong loong or {@code null} for none
   */
  public TweetLocBean setLoong(java.lang.Double loong) {
    this.loong = loong;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getMobileNumber() {
    return mobileNumber;
  }

  /**
   * @param mobileNumber mobileNumber or {@code null} for none
   */
  public TweetLocBean setMobileNumber(java.lang.String mobileNumber) {
    this.mobileNumber = mobileNumber;
    return this;
  }

  @Override
  public TweetLocBean set(String fieldName, Object value) {
    return (TweetLocBean) super.set(fieldName, value);
  }

  @Override
  public TweetLocBean clone() {
    return (TweetLocBean) super.clone();
  }

}