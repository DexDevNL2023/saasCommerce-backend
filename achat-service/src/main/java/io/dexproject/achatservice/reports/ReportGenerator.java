package com.dexproject.shop.api.reports;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import com.dexproject.shop.api.utils.MyUtils;

@Service
public class ReportGenerator {

  private final InstitutionService institutionService;

  public ReportGenerator(InstitutionService institutionService) {
    this.institutionService = institutionService;
  }

  public String generateFormulairePreInscriptionReportToPdf(PreInscriptionDTO preinscription) throws JRException, IOException {
    String displaName = preinscription.getNom() + " " + preinscription.getPrenom();
    InstitutionDTO institution = institutionService.getCurrentInstitution();
    // charge le fichier et compile-le
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("titre", "FORMULAIRE DE PRÉ-INSCRIPTION\n" +
      "PRE-REGISTRATION FORM");
    params.put("enteteGauche", institution.getEnteteGauche());
    params.put("enteteDroite", institution.getEnteteDroite());
    params.put("piedPage", institution.getPiedPage());
    params.put("displayName", displaName);
    params.put("nationnalite", preinscription.getNationalite());
    params.put("dateNaissance", preinscription.getDateNaissance());
    params.put("lieuNaissance", preinscription.getLieuNaissance());
    params.put("phone", preinscription.getTelephone());
    params.put("email", preinscription.getEmail());
    params.put("niveauFormationPreinscript", preinscription.getNiveau().getLibelle());
    params.put("anneeAcademique", preinscription.getAnneeAcademique());
    params.put("diplome", preinscription.getDiplome().getLibelle());
    params.put("noteObtenue", preinscription.getNote());
    params.put("anneeObtension", preinscription.getAnnee());
    params.put("etablissementObtension", preinscription.getEtablissement());
    int i = 0;
    for (FiliereDTO item : preinscription.getFilieres()) {
      if (i == 0) {
        params.put("filiereChoisie1", item.getLibelle());
      } else if (i == 1) {
        params.put("filiereChoisie2", item.getLibelle());
      } else if (i == 2) {
        params.put("filiereChoisie3", item.getLibelle());
      }
      i++;
    }

    //JasperReport report = JasperCompileManager.compileReport(ResourceUtils.getFile("classpath:formulaire-preinscription.jrxml").getAbsolutePath());
    JasperReport report = JasperCompileManager.compileReport(new ClassPathResource("formulaire-preinscription.jrxml").getInputStream());
    //JasperReport report = JasperCompileManager.compileReport(ClassLoader.getSystemResourceAsStream("formulaire-preinscription.jrxml"));
    JasperPrint print = JasperFillManager.fillReport(report, params);

    // crée un dossier pour stocker le rapport
    displaName = displaName.replace(" ", "_");
    String fileName = "/"+displaName+".pdf";
    Path uploadPath = getReportPath(print, fileName);

    // créer une méthode privée qui renvoie le lien vers le fichier pdf spécifique
    //String filePath = uploadPath.toAbsolutePath().toUri().toString()+fileName;
    String filePath = uploadPath.toAbsolutePath().toString()+fileName;
    //String filePath = uploadPath.toUri().toString()+fileName;
    //String realPath = getServletContext().getRealPath("/");
    //String filePath = uploadPath.toString()+fileName;

    return filePath;
  }

  public String generateFormulaireReinscriptionReportToPdf(ReinscriptionDTO reinscription) throws JRException, IOException {
      String displaName = reinscription.getEtudiant().getNom() + " " + reinscription.getEtudiant().getPrenom();
      InstitutionDTO institution = institutionService.getCurrentInstitution();
      // charge le fichier et compile-le
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("titre", "FORMULAIRE DE RÉ-INSCRIPTION\n" +
        "RE-REGISTRATION FORM");
      params.put("enteteGauche", institution.getEnteteGauche());
      params.put("enteteDroite", institution.getEnteteDroite());
      params.put("piedPage", institution.getPiedPage());
      params.put("displayName", displaName);
      params.put("nationnalite", reinscription.getEtudiant().getNationalite());
      params.put("dateNaissance", reinscription.getEtudiant().getDateNaissance());
      params.put("lieuNaissance", reinscription.getEtudiant().getLieuNaissance());
      params.put("phone", reinscription.getEtudiant().getTelephone());
      params.put("email", reinscription.getEtudiant().getEmail());
      params.put("niveauFormationPreinscript", reinscription.getNiveau().getLibelle());
      params.put("anneeAcademique", reinscription.getAnneeAcademique());
      params.put("diplome", reinscription.getDiplome().getLibelle());
      params.put("noteObtenue", reinscription.getNote());
      params.put("anneeObtension", reinscription.getAnnee());
      params.put("etablissementObtension", reinscription.getEtablissement());
      int i = 0;
      for (FiliereDTO item : reinscription.getFilieres()) {
          if (i == 0) {
              params.put("filiereChoisie1", item.getLibelle());
          } else if (i == 1) {
              params.put("filiereChoisie2", item.getLibelle());
          } else if (i == 2) {
              params.put("filiereChoisie3", item.getLibelle());
          }
          i++;
      }

      //JasperReport report = JasperCompileManager.compileReport(ResourceUtils.getFile("classpath:formulaire-preinscription.jrxml").getAbsolutePath());
      JasperReport report = JasperCompileManager.compileReport(new ClassPathResource("formulaire-preinscription.jrxml").getInputStream());
      //JasperReport report = JasperCompileManager.compileReport(ClassLoader.getSystemResourceAsStream("formulaire-preinscription.jrxml"));
      JasperPrint print = JasperFillManager.fillReport(report, params);

      // crée un dossier pour stocker le rapport
      displaName = displaName.replace(" ", "_");
      String fileName = "/"+displaName+".pdf";
      Path uploadPath = getReportPath(print, fileName);

      // créer une méthode privée qui renvoie le lien vers le fichier pdf spécifique
      //String filePath = uploadPath.toAbsolutePath().toUri().toString()+fileName;
      String filePath = uploadPath.toAbsolutePath().toString()+fileName;
      //String filePath = uploadPath.toUri().toString()+fileName;
      //String realPath = getServletContext().getRealPath("/");
      //String filePath = uploadPath.toString()+fileName;

      return filePath;
  }

  private Path getReportPath(JasperPrint jasperPrint, String fileName) throws IOException, JRException {
      String uploadDir = StringUtils.cleanPath("./generated-reports");
      Path uploadPath = Paths.get(uploadDir);
      if (!Files.exists(uploadPath)){
          Files.createDirectories(uploadPath);
      }

      // générer le rapport et l'enregistrer dans le dossier qui vient d'être créé
      JasperExportManager.exportReportToPdfFile(jasperPrint, uploadPath+fileName);

      return uploadPath;
  }
}
