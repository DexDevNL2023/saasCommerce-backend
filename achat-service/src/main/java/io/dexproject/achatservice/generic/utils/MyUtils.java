package io.dexproject.achatservice.generic.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.internal.bytebuddy.utility.RandomString;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public class MyUtils {

  public static String upperCaseTrim(String str) {
    return str == null ? null : str.toUpperCase().trim();
  }

  public static String lowerCaseTrim(String str) {
    return str == null ? null : str.toLowerCase().trim();
  }

  public static Date calculExpiredDate(Date saved_date, int minute) {
    Calendar c = Calendar.getInstance();
    c.setTime(saved_date);
    c.add(Calendar.MINUTE, minute);
    return c.getTime();
  }

  public static String RandGeneratedKey(String str, int l) {
    String AlphaNumericStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789";

    StringBuilder sb = new StringBuilder(l);
    for (int i = 0; i < l; i++) {
      int index = (int) (AlphaNumericStr.length() * Math.random());
      sb.append(AlphaNumericStr.charAt(index));
    }
    String key = str + sb;

    return key;
  }

  public static String GenerateToken(int token) {
    String AlphaNumericStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    StringBuilder sb = new StringBuilder(token);
    for (int i = 0; i < token; i++) {
      int index = (int) (AlphaNumericStr.length() * Math.random());
      sb.append(AlphaNumericStr.charAt(index));
    }
    return sb.toString();
  }

  public static Instant now() {
    return Instant.now().truncatedTo(ChronoUnit.MICROS);
  }

    public static Instant dateNow() {
        return Instant.from(now());
  }

  public static Date currentDate() {
    return Date.from(now());
  }

  public static String generatedPassWord() {
    String AlphaNumericStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789";

    StringBuilder sb = new StringBuilder(8);
    for (int i = 0; i < 8; i++) {
      int index = (int) (AlphaNumericStr.length() * Math.random());
      sb.append(AlphaNumericStr.charAt(index));
    }
    String pass = sb.toString();
    return pass;
  }

  public static String GenerateCode(String prefixe) {
    String randomCode = RandomString.make(64);
    String number = String.format("%04d", randomCode.charAt(10000));
    return prefixe+"-"+number;
  }

  public static String GenerateMatricule(String prefixe) {
    DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits

    Random random = new Random();
    String number = String.format("%05d", random.nextInt(100000));
    return prefixe+"-"+number;
  }

  private static final String URL_REGEX =
    "^(www.)" + "(%[0-9A-Fa-f]{2}|[-()_.!~*';/?:@&=+$,A-Za-z0-9])" +
      "([).!';/?:,][[:blank:]])?$";

  private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

  public static boolean urlValidator(String url) {
    try {
      new URI(url).parseServerAuthority();
      return true;
    } catch (URISyntaxException e) {
      return false;
    }
  }

  public static final String getSiteURL(HttpServletRequest request){
    String siteUrl = request.getRequestURL().toString();
    return siteUrl.replace(request.getServletPath(), "");
  }

  public static final String getBasicAuthenticationHeader(String uuid, String key) {
    String valueToEncode = uuid + ":" + key;
    return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
  }

  public static int calculAge(Date dateNaissance){
    Calendar current = Calendar.getInstance();
    Calendar birthday = Calendar.getInstance();
    birthday.setTime(dateNaissance);
    int yearDiff = current.get((Calendar.YEAR) - birthday.get(Calendar.YEAR));
    if(birthday.after(current)){
      yearDiff = yearDiff - 1;
    }
    return yearDiff;
  }

  public static String getStringFromHtml(HttpServletRequest request, String link) {
    try {
      String siteUrl = request.getRequestURL().toString();
      URL url = new URL(siteUrl+link);
      System.out.println(url);
      URLConnection con = url.openConnection();
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      StringBuilder html = new StringBuilder();
      String val;
      while ((val=in.readLine())!=null) {
        html.append(val);
      }
      String result = html.toString();
      in.close();
      return result;
    }catch (Exception ex) {
      System.out.println(ex.getMessage());
      return null;
    }
  }

  public static String UniqueId() {
    String uniqueId = "";
    uniqueId = UUID.randomUUID().toString();
    uniqueId = uniqueId.replace("-", "");
    return uniqueId;
  }
}
