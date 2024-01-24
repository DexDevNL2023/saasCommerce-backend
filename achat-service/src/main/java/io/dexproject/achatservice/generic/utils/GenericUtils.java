package io.dexproject.achatservice.generic.utils;

import io.dexproject.achatservice.generic.exceptions.ResourceNotFoundException;
import io.dexproject.achatservice.generic.security.crud.entities.enums.RoleName;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.internal.bytebuddy.utility.RandomString;
import org.springframework.core.env.Environment;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class GenericUtils {

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

  public static String GenerateNumOrder(String prefixe) {
    String randomCode = RandomString.make(64);
    String number = String.format("%04d", randomCode.charAt(10000));
    return prefixe+"-"+number;
  }

  public static String GenerateCode() {
    String randomCode = RandomString.make(8);
    return String.format("%04d", randomCode.charAt(10000));
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

  public static List<SimpleGrantedAuthority> buildSimpleGrantedAuthorities(final RoleName role) {
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(role.getLabel()));
    return authorities;
  }

  public static void validatePageNumberAndSize(final Integer page, final Integer size) {
    if(page < 0) {
      throw new ResourceNotFoundException("Le numéro de page ne peut pas être inférieur à zéro.");
    }

    if(size > AppConstants.MAX_PAGE_SIZE) {
      throw new ResourceNotFoundException("La taille de la page ne doit pas être supérieure à " + AppConstants.MAX_PAGE_SIZE);
    }
  }

  public static Date calculateExpiryDate(final int expiryTimeInMinutes) {
    final Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(new Date().getTime());
    cal.add(Calendar.MINUTE, expiryTimeInMinutes);
    return new Date(cal.getTime().getTime());
  }

  public static String generateTokenNumber() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  public static Path FileStorageService(Environment env) {
    Path fileStorageLocation = Paths.get(env.getProperty("app.file.upload-dir", "./uploads/files"))
            .toAbsolutePath().normalize();

    try {
      return Files.createDirectories(fileStorageLocation);
    } catch (Exception ex) {
      throw new RuntimeException(
              "Impossible de créer le répertoire dans lequel les fichiers téléchargés seront stockés.", ex);
    }
  }

  private static String getFileExtension(String fileName) {
    if (fileName == null) {
      return null;
    }
    String[] fileNameParts = fileName.split("\\.");

    return fileNameParts[fileNameParts.length - 1];
  }

  public static String storeFile(MultipartFile file, Path fileStorageLocation) {
    // Normalize file name
    String fileName =
            new Date().getTime() + "-file." + getFileExtension(file.getOriginalFilename());

    try {
      // Check if the filename contains invalid characters
      if (fileName.contains("..")) {
        throw new RuntimeException(
                "Désolé! Le nom du fichier contient une séquence de chemin non valide" + fileName);
      }

      Path targetLocation = fileStorageLocation.resolve(fileName);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

      return fileName;
    } catch (IOException ex) {
      throw new RuntimeException("Impossible de stocker le fichier " + fileName + ". Veuillez réessayer!", ex);
    }
  }

  public static String verifieFormatLangue(String langKey) {
    return switch (langKey) {
        case "En" -> "En";
        case "Esp" -> "Esp";
        default -> "Fr"; // default language
    };
  }

  public static boolean isValidEmailAddress(String email) {
    boolean result = true;
    try {
      InternetAddress emailAddr = new InternetAddress(email);
      emailAddr.validate();
    } catch (AddressException ex) {
      result = false;
    }
    return result;
  }
}
