package com.dexproject.shop.api.reports;

import java.io.IOException;

import net.sf.jasperreports.engine.JRException;

public interface ReportService {
    String generateFormulairePreInscriptionReport(PreinscriptionDTO preinscription) throws JRException, IOException;
}
