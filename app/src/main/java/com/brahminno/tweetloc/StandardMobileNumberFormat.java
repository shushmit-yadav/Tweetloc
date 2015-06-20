package com.brahminno.tweetloc;

import android.util.Log;

/**
 * Created by Shushmit on 20-06-2015.
 * Brahmastra Innovations....
 * this class is used for making mobile number in standard formate with countery code......
 */
public class StandardMobileNumberFormat {

    String countryCode;
    String standardMobileNumber;

    public String getLocale(String mobileNumber, String locale) {

        switch (locale) {
            case "AF":
                countryCode = "+93";
                break;
            case "AL":
                countryCode = "+355";
                break;
            case "DZ":
                countryCode = "+213";
                break;
            case "AS":
                countryCode = "+1 684";
                break;
            case "AD":
                countryCode = "+376";
                break;
            case "AO":
                countryCode = "+244";
                break;
            case "AI":
                countryCode = "+1 264";
                break;
            case "AQ":
                countryCode = "+672";
                break;
            case "AG":
                countryCode = "+1268";
                break;
            case "AR":
                countryCode = "+54";
                break;
            case "AM":
                countryCode = "+374";
                break;
            case "AW":
                countryCode = "+297";
                break;
            case "AU":
                countryCode = "+61";
                break;
            case "AT":
                countryCode = "+43";
                break;
            case "AZ":
                countryCode = "+994";
                break;
            case "BS":
                countryCode = "+1 242";
                break;
            case "BH":
                countryCode = "+973";
                break;
            case "BD":
                countryCode = "+880";
                break;
            case "BB":
                countryCode = "+1 246";
                break;
            case "BY":
                countryCode = "+375";
                break;
            case "BE":
                countryCode = "+32";
                break;
            case "BZ":
                countryCode = "+501";
                break;
            case "BJ":
                countryCode = "+229";
                break;
            case "BM":
                countryCode = "+1 441";
                break;
            case "BT":
                countryCode = "+975";
                break;
            case "BO":
                countryCode = "+591";
                break;
            case "BA":
                countryCode = "+387";
                break;
            case "BW":
                countryCode = "+267";
                break;
            case "BR":
                countryCode = "+55";
                break;
            case "IO":
                countryCode = "+246";
                break;
            case "BN":
                countryCode = "+673";
                break;
            case "BG":
                countryCode = "+359";
                break;
            case "BF":
                countryCode = "+226";
                break;
            case "BI":
                countryCode = "+257";
                break;
            case "KH":
                countryCode = "+855";
                break;
            case "CM":
                countryCode = "+237";
                break;
            case "CA":
                countryCode = "+1";
                break;
            case "CV":
                countryCode = "+238";
                break;
            case "KY":
                countryCode = "+ 345";
                break;
            case "CF":
                countryCode = "+236";
                break;
            case "TD":
                countryCode = "+235";
                break;
            case "CL":
                countryCode = "+56";
                break;
            case "CN":
                countryCode = "+86";
                break;
            case "CX":
                countryCode = "+61";
                break;
            case "CC":
                countryCode = "+61";
                break;
            case "CO":
                countryCode = "+57";
                break;
            case "KM":
                countryCode = "+269";
                break;
            case "CG":
                countryCode = "+242";
                break;
            case "CD":
                countryCode = "+243";
                break;
            case "CK":
                countryCode = "+682";
                break;
            case "CR":
                countryCode = "+506";
                break;
            case "CI":
                countryCode = "+225";
                break;
            case "HR":
                countryCode = "+385";
                break;
            case "CU":
                countryCode = "+53";
                break;
            case "CY":
                countryCode = "+357";
                break;
            case "CZ":
                countryCode = "+420";
                break;
            case "DK":
                countryCode = "+45";
                break;
            case "DJ":
                countryCode = "+253";
                break;
            case "DM":
                countryCode = "+1 767";
                break;
            case "DO":
                countryCode = "+1 849";
                break;
            case "EC":
                countryCode = "+593";
                break;
            case "EG":
                countryCode = "+20";
                break;
            case "SV":
                countryCode = "+503";
                break;
            case "GQ":
                countryCode = "+240";
                break;
            case "ER":
                countryCode = "+291";
                break;
            case "EE":
                countryCode = "+372";
                break;
            case "ET":
                countryCode = "+251";
                break;
            case "FK":
                countryCode = "+500";
                break;
            case "FO":
                countryCode = "+298";
                break;
            case "FJ":
                countryCode = "+679";
                break;
            case "FI":
                countryCode = "+358";
                break;
            case "FR":
                countryCode = "+33";
                break;
            case "GF":
                countryCode = "+594";
                break;
            case "PF":
                countryCode = "+689";
                break;
            case "GA":
                countryCode = "+241";
                break;
            case "GM":
                countryCode = "+220";
                break;
            case "GE":
                countryCode = "+995";
                break;
            case "DE":
                countryCode = "+49";
                break;
            case "GH":
                countryCode = "+233";
                break;
            case "GI":
                countryCode = "+350";
                break;
            case "GR":
                countryCode = "+30";
                break;
            case "GL":
                countryCode = "+299";
                break;
            case "GD":
                countryCode = "+1 473";
                break;
            case "GP":
                countryCode = "+590";
                break;
            case "GU":
                countryCode = "+1 671";
                break;
            case "GT":
                countryCode = "+502";
                break;
            case "GG":
                countryCode = "+44";
                break;
            case "GN":
                countryCode = "+224";
                break;
            case "GW":
                countryCode = "+245";
                break;
            case "GY":
                countryCode = "+595";
                break;
            case "HT":
                countryCode = "+509";
                break;
            case "VA":
                countryCode = "+379";
                break;
            case "HN":
                countryCode = "+504";
                break;
            case "HK":
                countryCode = "+852";
                break;
            case "HU":
                countryCode = "+36";
                break;
            case "IS":
                countryCode = "+354";
                break;
            case "IN":
                countryCode = "+91";
                break;
            case "ID":
                countryCode = "+62";
                break;
            case "IR":
                countryCode = "+98";
                break;
            case "IQ":
                countryCode = "+964";
                break;
            case "IE":
                countryCode = "+353";
                break;
            case "IM":
                countryCode = "+44";
                break;
            case "IL":
                countryCode = "+972";
                break;
            case "IT":
                countryCode = "+39";
                break;
            case "JM":
                countryCode = "+1 876";
                break;
            case "JP":
                countryCode = "+81";
                break;
            case "JE":
                countryCode = "+44";
                break;
            case "JO":
                countryCode = "+962";
                break;
            case "KZ":
                countryCode = "+7 7";
                break;
            case "KE":
                countryCode = "+254";
                break;
            case "KI":
                countryCode = "+686";
                break;
            case "KP":
                countryCode = "+850";
                break;
            case "KR":
                countryCode = "+82";
                break;
            case "KW":
                countryCode = "+965";
                break;
            case "KG":
                countryCode = "+996";
                break;
            case "LA":
                countryCode = "+856";
                break;
            case "LV":
                countryCode = "+371";
                break;
            case "LB":
                countryCode = "+961";
                break;
            case "LS":
                countryCode = "+266";
                break;
            case "LR":
                countryCode = "+231";
                break;
            case "LY":
                countryCode = "+218";
                break;
            case "LI":
                countryCode = "+423";
                break;
            case "LT":
                countryCode = "+370";
                break;
            case "LU":
                countryCode = "+352";
                break;
            case "MO":
                countryCode = "+853";
                break;
            case "MK":
                countryCode = "+389";
                break;
            case "MG":
                countryCode = "+261";
                break;
            case "MW":
                countryCode = "+265";
                break;
            case "MY":
                countryCode = "+60";
                break;
            case "MV":
                countryCode = "+960";
                break;
            case "ML":
                countryCode = "+223";
                break;
            case "MT":
                countryCode = "+356";
                break;
            case "MH":
                countryCode = "+692";
                break;
            case "MQ":
                countryCode = "+596";
                break;
            case "MR":
                countryCode = "+222";
                break;
            case "MU":
                countryCode = "+230";
                break;
            case "YT":
                countryCode = "+262";
                break;
            case "MX":
                countryCode = "+52";
                break;
            case "FM":
                countryCode = "+691";
                break;
            case "MD":
                countryCode = "+373";
                break;
            case "MC":
                countryCode = "+377";
                break;
            case "MN":
                countryCode = "+976";
                break;
            case "ME":
                countryCode = "+382";
                break;
            case "MS":
                countryCode = "+1664";
                break;
            case "MA":
                countryCode = "+212";
                break;
            case "MZ":
                countryCode = "+258";
                break;
            case "MM":
                countryCode = "+95";
                break;
            case "NA":
                countryCode = "+264";
                break;
            case "NR":
                countryCode = "+674";
                break;
            case "NP":
                countryCode = "+977";
                break;
            case "NL":
                countryCode = "+31";
                break;
            case "AN":
                countryCode = "+599";
                break;
            case "NC":
                countryCode = "+687";
                break;
            case "NZ":
                countryCode = "+64";
                break;
            case "NI":
                countryCode = "+505";
                break;
            case "NE":
                countryCode = "+227";
                break;
            case "NG":
                countryCode = "+234";
                break;
            case "NU":
                countryCode = "+683";
                break;
            case "NF":
                countryCode = "+672";
                break;
            case "MP":
                countryCode = "+1 670";
                break;
            case "NO":
                countryCode = "+47";
                break;
            case "OM":
                countryCode = "+968";
                break;
            case "PK":
                countryCode = "+92";
                break;
            case "PW":
                countryCode = "+680";
                break;
            case "PS":
                countryCode = "+970";
                break;
            case "PA":
                countryCode = "+507";
                break;
            case "PG":
                countryCode = "+675";
                break;
            case "PY":
                countryCode = "+595";
                break;
            case "PE":
                countryCode = "+51";
                break;
            case "PH":
                countryCode = "+63";
                break;
            case "PN":
                countryCode = "+872";
                break;
            case "PL":
                countryCode = "+48";
                break;
            case "PT":
                countryCode = "+351";
                break;
            case "PR":
                countryCode = "+1 939";
                break;
            case "QA":
                countryCode = "+974";
                break;
            case "RO":
                countryCode = "+40";
                break;
            case "RU":
                countryCode = "+7";
                break;
            case "RW":
                countryCode = "+250";
                break;
            case "RE":
                countryCode = "+262";
                break;
            case "BL":
                countryCode = "+590";
                break;
            case "SH":
                countryCode = "+290";
                break;
            case "KN":
                countryCode = "+1 869";
                break;
            case "LC":
                countryCode = "+1 758";
                break;
            case "MF":
                countryCode = "+590";
                break;
            case "PM":
                countryCode = "+508";
                break;
            case "VC":
                countryCode = "+1 784";
                break;
            case "WS":
                countryCode = "+685";
                break;
            case "SM":
                countryCode = "+378";
                break;
            case "ST":
                countryCode = "+239";
                break;
            case "SA":
                countryCode = "+966";
                break;
            case "SN":
                countryCode = "+221";
                break;
            case "RS":
                countryCode = "+381";
                break;
            case "SC":
                countryCode = "+248";
                break;
            case "SL":
                countryCode = "+232";
                break;
            case "SG":
                countryCode = "+65";
                break;
            case "SK":
                countryCode = "+421";
                break;
            case "SI":
                countryCode = "+386";
                break;
            case "SB":
                countryCode = "+677";
                break;
            case "SO":
                countryCode = "+252";
                break;
            case "ZA":
                countryCode = "+27";
                break;
            case "GS":
                countryCode = "+500";
                break;
            case "ES":
                countryCode = "+34";
                break;
            case "LK":
                countryCode = "+94";
                break;
            case "SD":
                countryCode = "+249";
                break;
            case "SR":
                countryCode = "+597";
                break;
            case "SJ":
                countryCode = "+47";
                break;
            case "SZ":
                countryCode = "+268";
                break;
            case "SE":
                countryCode = "+46";
                break;
            case "CH":
                countryCode = "+41";
                break;
            case "SY":
                countryCode = "+963";
                break;
            case "TW":
                countryCode = "+886";
                break;
            case "TJ":
                countryCode = "+992";
                break;
            case "TZ":
                countryCode = "+255";
                break;
            case "TH":
                countryCode = "+66";
                break;
            case "TL":
                countryCode = "+670";
                break;
            case "TG":
                countryCode = "+228";
                break;
            case "TK":
                countryCode = "+690";
                break;
            case "TO":
                countryCode = "+676";
                break;
            case "TT":
                countryCode = "+1 868";
                break;
            case "TN":
                countryCode = "+216";
                break;
            case "TR":
                countryCode = "+90";
                break;
            case "TM":
                countryCode = "+993";
                break;
            case "TC":
                countryCode = "+1 649";
                break;
            case "TV":
                countryCode = "+688";
                break;
            case "UG":
                countryCode = "+256";
                break;
            case "UA":
                countryCode = "+380";
                break;
            case "AE":
                countryCode = "+971";
                break;
            case "GB":
                countryCode = "+44";
                break;
            case "US":
                countryCode = "+1";
                break;
            case "UY":
                countryCode = "+598";
                break;
            case "UZ":
                countryCode = "+998";
                break;
            case "VU":
                countryCode = "+678";
                break;
            case "VE":
                countryCode = "+58";
                break;
            case "VN":
                countryCode = "+84";
                break;
            case "VG":
                countryCode = "+1 284";
                break;
            case "VI":
                countryCode = "+1 340";
                break;
            case "WF":
                countryCode = "+681";
                break;
            case "YE":
                countryCode = "+967";
                break;
            case "ZM":
                countryCode = "+260";
                break;
            case "ZW":
                countryCode = "+263";
                break;
            case "AX":
                countryCode = "+358";
                break;
        }

        mobileNumber = mobileNumber.trim();
        mobileNumber = mobileNumber.replace(" ", "");
        try{
            Log.i("After trim", mobileNumber);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        if (mobileNumber.startsWith("+"))
            standardMobileNumber = mobileNumber;

        if (mobileNumber.startsWith("0"))
            standardMobileNumber = countryCode + mobileNumber.substring(1, mobileNumber.length());

        if (mobileNumber.startsWith("1") || mobileNumber.startsWith("2") || mobileNumber.startsWith("3") || mobileNumber.startsWith("4") || mobileNumber.startsWith("5") || mobileNumber.startsWith("6") || mobileNumber.startsWith("7") || mobileNumber.startsWith("8") || mobileNumber.startsWith("9"))
            standardMobileNumber = countryCode + mobileNumber;

        //standardMobileNumber.replaceAll("\\s+","");
        try {
            Log.i("standard ..", "" + standardMobileNumber);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        return standardMobileNumber;
    }
}
