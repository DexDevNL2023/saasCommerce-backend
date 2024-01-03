package net.ktccenter.campusApi.utils;

import lombok.SneakyThrows;
import net.ktccenter.campusApi.dto.reponse.administration.AnneeAcademiqueDTO;
import net.ktccenter.campusApi.dto.reponse.administration.InstitutionDTO;
import net.ktccenter.campusApi.entities.profile.User;
import net.ktccenter.campusApi.service.administration.AnneeAcademiqueService;
import net.ktccenter.campusApi.service.administration.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@Slf4j
public class MyUtils {

public static String upperCaseTrim(String str) {
    return str == null ? null : str.toUpperCase().trim();
}

public static String lowerCaseTrim(String str) {
    return str == null ? null : str.toLowerCase().trim();
}

@Autowired
private static InstitutionService institutionService;

@Autowired
private static AnneeAcademiqueService anneeAcademiqueService;

@Autowired
private  static JavaMailSender javaMailSender;

@Value("${app.template.url}")
private static String APP_TEMPLATE_URL;

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
    String key = str + "" + sb.toString();

    return key;
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

public static String GenerateToken(int token) {
    String AlphaNumericStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    StringBuilder sb = new StringBuilder(token);
    for (int i = 0; i < token; i++) {
        int index = (int) (AlphaNumericStr.length() * Math.random());
        sb.append(AlphaNumericStr.charAt(index));
    }
    return sb.toString();
}

public static String GenerateCodeInscription(String prefixe) {
    DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
    String annee = df.format(Calendar.getInstance().getTime());

    Random random = new Random();
    String number = String.format("%04d", random.nextInt(10000));
    return annee+"-"+prefixe+""+number;
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

public static String getStringFromHtml(String link) {
    try {
        URL url = new URL(APP_TEMPLATE_URL+link);
        System.out.println(url.toString());
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

@SneakyThrows
public static InstitutionDTO getCurrentInstitution() {
  if(institutionService.getCurrentInstitution() == null) {
    return null;
  }

  return institutionService.getCurrentInstitution();
}

@SneakyThrows
public static AnneeAcademiqueDTO getCurrentSession() {
  if(anneeAcademiqueService.getCurrentAcademicYear() == null) {
    return null;
  }

  return anneeAcademiqueService.getCurrentAcademicYear();
}

@SneakyThrows
public static String sendMail(User user, String subject, String description, String btnText, String URL, String token) throws MessagingException, UnsupportedEncodingException {
    try{
        InstitutionDTO parametre = getCurrentInstitution();
        String toAddress = user.getEmail();
        String fromAddress = parametre.getEmail();
        String senderName = parametre.getName();
        String content = getStringFromHtml("validation.html");
        log.info(content);
        if(content == null){
            return "Template not found or invalid, please contact the administrator";
        }
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getUsername());
        content = content.replace("[[TYPE]]", subject);
        content = content.replace("[[TEXT]]", description);//"Click on the button below to reset your password on our online payment platform");
        content = content.replace("[[BTN_TEXT]]", btnText);
        content = content.replace("[[TELEPHONE]]", parametre.getTelephone());
        content = content.replace("[[WEBSITE]]",  parametre.getSite());
        content = content.replace("[[ADDRESS]]",  parametre.getAdresse());
        content = content.replace("[[APP_NAME]]",  senderName);

        String verifyUrl = URL + token;
        content = content.replace("[[URL]]", verifyUrl);
        helper.setText(content, true);
        javaMailSender.send(message);
        return "OK";
    }catch (Exception e){
        return e.getMessage();
    }
  }
}

