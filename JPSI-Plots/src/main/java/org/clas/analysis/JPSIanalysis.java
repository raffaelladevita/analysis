/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clas.analysis;

import java.util.ArrayList;
import org.clas.viewer.AnalysisMonitor;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.clas.physics.Particle;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.math.F1D;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;

/**
 *
 * @author devita
 */
public class JPSIanalysis extends AnalysisMonitor {
    
    private int nEvent, nGen, nElec, nIM, nIM_epem, nMM, nMM_ep, nMM_em, nMM_epem, nMM_pepem;
    private boolean ft;

    public JPSIanalysis(String name) {
        super(name);
        this.setAnalysisTabNames("Generated", "Reconstructed", "Resolution","Invariant Mass","Missing Mass (FT)","Electrons", "Protons");
        this.init(false);
        
        nEvent   = 0;
        nGen     = 0;
        nElec    = 0;
        nIM      = 0;
        nIM_epem = 0;
        nMM      = 0;
        nMM_ep   = 0;
        nMM_em   = 0;
        nMM_epem = 0;
        ft     = false;
    }

    
    @Override
    public void createHistos() {
        // initialize canvas and create histograms
        this.setNumberOfEvents(0);
        H1F summary = new H1F("summary","summary",6,1,7);
        summary.setTitleX("sector");
        summary.setTitleY("DC hits");
        summary.setFillColor(33);
        DataGroup sum = new DataGroup(1,1);
        sum.addDataSet(summary, 0);
        this.setAnalysisSummary(sum);
        // Generated
        H2F hi_gen_q2w = new H2F("hi_gen_q2w","hi_gen_q2w",100, 4., 4.8, 100, 0., 2.); 
        hi_gen_q2w.setTitleX("W (GeV)");
        hi_gen_q2w.setTitleY("Q2 (GeV2)");
        H1F hi_gen_w = new H1F("hi_gen_w","hi_gen_w",100, 4., 4.8); 
        hi_gen_w.setTitleX("W (GeV)");
        hi_gen_w.setTitleY("Counts");
        hi_gen_w.setOptStat("1110"); 
        H2F hi_gen_el = new H2F("hi_gen_el","hi_gen_el",100, 0., 3., 100, 0., 50.);
        hi_gen_el.setTitleX("p (GeV)");
        hi_gen_el.setTitleY("#theta (deg)");
        hi_gen_el.setTitle("Electron");
        H2F hi_gen_pr = new H2F("hi_gen_pr","hi_gen_pr",100, 0., 6., 100, 0., 35.);
        hi_gen_pr.setTitleX("p (GeV)");
        hi_gen_pr.setTitleY("#theta (deg)");
        hi_gen_pr.setTitle("Proton");
        H2F hi_gen_ep = new H2F("hi_gen_ep","hi_gen_ep",100, 0., 8., 100, 0., 50.);
        hi_gen_ep.setTitleX("p (GeV)");
        hi_gen_ep.setTitleY("#theta (deg)");
        hi_gen_ep.setTitle("l+");
        H2F hi_gen_em = new H2F("hi_gen_em","hi_gen_em",100, 0., 8., 100, 0., 50.);
        hi_gen_em.setTitleX("p (GeV)");
        hi_gen_em.setTitleY("#theta (deg)");
        hi_gen_em.setTitle("l-");
        H1F hi_gen_eg = new H1F("hi_gen_eg","hi_gen_eg",100, 8., 11.5); 
        hi_gen_eg.setTitleX("E#gamma (GeV)");
        hi_gen_eg.setTitleY("Counts");
        hi_gen_eg.setOptStat("1110");
        DataGroup dg_generated = new DataGroup(4,2);
        dg_generated.addDataSet(hi_gen_w,  0);
        dg_generated.addDataSet(hi_gen_q2w, 1);
        dg_generated.addDataSet(hi_gen_el, 2);
        dg_generated.addDataSet(hi_gen_pr, 3);
        dg_generated.addDataSet(hi_gen_ep, 4);
        dg_generated.addDataSet(hi_gen_em, 5);
        dg_generated.addDataSet(hi_gen_eg, 6);
        this.getDataGroup().add(dg_generated, 1);
        // Reconstructed
        H2F hi_rec_el = new H2F("hi_rec_el","hi_rec_el",100, 0., 3., 100, 0., 50.);
        hi_rec_el.setTitleX("p (GeV)");
        hi_rec_el.setTitleY("#theta (deg)");
        hi_rec_el.setTitle("Electron");
        H2F hi_rec_pr = new H2F("hi_rec_pr","hi_rec_pr",100, 0., 6., 100, 0., 35.);
        hi_rec_pr.setTitleX("p (GeV)");
        hi_rec_pr.setTitleY("#theta (deg)");
        hi_rec_pr.setTitle("Proton");
        H2F hi_rec_ep = new H2F("hi_rec_ep","hi_rec_ep",100, 0., 8., 100, 0., 50.);
        hi_rec_ep.setTitleX("p (GeV)");
        hi_rec_ep.setTitleY("#theta (deg)");
        hi_rec_ep.setTitle("l+");
        H2F hi_rec_em = new H2F("hi_rec_em","hi_rec_em",100, 0., 8., 100, 0., 50.);
        hi_rec_em.setTitleX("p (GeV)");
        hi_rec_em.setTitleY("#theta (deg)");
        hi_rec_em.setTitle("l-");
        DataGroup dg_reconstructed = new DataGroup(2,2);
        dg_reconstructed.addDataSet(hi_rec_el, 0);
        dg_reconstructed.addDataSet(hi_rec_pr, 1);
        dg_reconstructed.addDataSet(hi_rec_ep, 2);
        dg_reconstructed.addDataSet(hi_rec_em, 3);
        this.getDataGroup().add(dg_reconstructed, 2);
        // Resolution
        H1F hi_res_el = new H1F("hi_res_el","hi_res_el",200, -0.5, 0.5);
        hi_res_el.setTitleX("p (GeV)");
        hi_res_el.setTitleY("Counts");
        hi_res_el.setTitle("Electron");
        hi_res_el.setOptStat("1110");    
        H1F hi_res_pr = new H1F("hi_res_pr","hi_res_pr",200, -0.5, 0.5);
        hi_res_pr.setTitleX("p (GeV)");
        hi_res_pr.setTitleY("Counts");
        hi_res_pr.setTitle("Proton");
        hi_res_pr.setOptStat("1110");    
        H1F hi_res_ep = new H1F("hi_res_ep","hi_res_ep",200, -0.5, 0.5);
        hi_res_ep.setTitleX("p (GeV)");
        hi_res_ep.setTitleY("Counts");
        hi_res_ep.setTitle("l+");
        hi_res_ep.setOptStat("1110");    
        H1F hi_res_em = new H1F("hi_res_em","hi_res_em",200, -0.5, 0.5);
        hi_res_em.setTitleX("p (GeV)");
        hi_res_em.setTitleY("Counts");
        hi_res_em.setTitle("l-");
        hi_res_em.setOptStat("1110");    
        DataGroup dg_resolution = new DataGroup(2,2);
        dg_resolution.addDataSet(hi_res_el, 0);
        dg_resolution.addDataSet(hi_res_pr, 1);
        dg_resolution.addDataSet(hi_res_ep, 2);
        dg_resolution.addDataSet(hi_res_em, 3);
        this.getDataGroup().add(dg_resolution, 3);
        // Invariant Mass
        H1F hi_im_eg = new H1F("hi_im_eg","hi_im_eg",100, 8., 11.5); 
        hi_im_eg.setTitleX("E#gamma (GeV)");
        hi_im_eg.setTitleY("Counts");
        hi_im_eg.setOptStat("1110");
        H1F hi_im_eg_acc = new H1F("hi_im_eg_acc","hi_im_eg_acc",100, 8., 11.5); 
        hi_im_eg_acc.setTitleX("E#gamma (GeV)");
        hi_im_eg_acc.setTitleY("Acceptance");
        hi_im_eg_acc.setOptStat("1110");
        H1F hi_im_w = new H1F("hi_im_w","hi_im_w",100, 4., 4.8); 
        hi_im_w.setTitleX("W (GeV)");
        hi_im_w.setTitleY("Counts");
        hi_im_w.setOptStat("1110");
        H2F hi_im_q2w = new H2F("hi_im_q2w","hi_im_q2w",100, 4., 4.8, 100, 0., 2.); 
        hi_im_q2w.setTitleX("W (GeV)");
        hi_im_q2w.setTitleY("Q2 (GeV2)");
        H1F hi_mepem = new H1F("hi_mepem","hi_mepem",200,2.8,3.4);         
        hi_mepem.setTitleX("M(l+l-) GeV)");
        hi_mepem.setTitleY("Counts");
        hi_mepem.setOptStat("1110");
        F1D f1_mepem = new F1D("f1_mepem","[amp]*gaus(x,[mean],[sigma])", 2.8, 3.4);
        f1_mepem.setParameter(0, 0);
        f1_mepem.setParameter(1, 3.1);
        f1_mepem.setParameter(2, 0.01);
        f1_mepem.setLineWidth(3);
        f1_mepem.setLineColor(4);
        f1_mepem.setOptStat("1111");
        H1F hi_im_emiss = new H1F("hi_im_emiss","hi_im_emiss",200, -0.1, 0.1);        
        hi_im_emiss.setTitleX("Mx(ep#rarrow p'l+l-X) (GeV)");
        hi_im_emiss.setTitleY("Counts");
        hi_im_emiss.setOptStat("1110");    
        H1F hi_im_mres = new H1F("hi_im_mres","hi_im_mres",200, -0.1, 0.1);        
        hi_im_mres.setTitleX("#DeltaM(p'l+l-) (GeV)");
        hi_im_mres.setTitleY("Counts");
        hi_im_mres.setOptStat("1110");    
        F1D f1_im_mres = new F1D("f1_im_mres","[amp]*gaus(x,[mean],[sigma])", -0.1, 0.1);
        f1_im_mres.setParameter(0, 0);
        f1_im_mres.setParameter(1, 0);
        f1_im_mres.setParameter(2, 0.01);
        f1_im_mres.setLineWidth(3);
        f1_im_mres.setLineColor(4);
        f1_im_mres.setOptStat("1111");
        H2F hi_im_mresvsm = new H2F("hi_im_mresvsm","hi_im_mresvsm",100, 4., 4.8, 100, -0.1, 0.1);        
        hi_im_mresvsm.setTitleX("M(p'l+l-) (GeV)");
        hi_im_mresvsm.setTitleY("#DeltaM(p'l+l-) (GeV)");
        DataGroup dg_invariant = new DataGroup(4,2);
        dg_invariant.addDataSet(hi_im_eg, 0);
        dg_invariant.addDataSet(hi_im_w, 1);
        dg_invariant.addDataSet(hi_mepem, 2);
        dg_invariant.addDataSet(f1_mepem, 3);
        dg_invariant.addDataSet(hi_im_mres, 3);
        dg_invariant.addDataSet(f1_im_mres, 3);
        dg_invariant.addDataSet(hi_im_eg_acc, 4);
        dg_invariant.addDataSet(hi_im_q2w, 5);
        dg_invariant.addDataSet(hi_im_emiss, 6);
        dg_invariant.addDataSet(hi_im_mresvsm, 7);
        this.getDataGroup().add(dg_invariant, 4);
        // Missing Mass (FT)
        H1F hi_mm_eg = new H1F("hi_mm_eg","hi_mm_eg",100, 8., 11.5); 
        hi_mm_eg.setTitleX("E#gamma (GeV)");
        hi_mm_eg.setTitleY("Counts");
        hi_mm_eg.setOptStat("1110");
        H2F hi_mm_q2w = new H2F("hi_mm_q2w","hi_mm_q2w",100, 4., 4.8, 100, 0., 0.2); 
        hi_mm_q2w.setTitleX("W (GeV)");
        hi_mm_q2w.setTitleY("Q2 (GeV2)");
        H1F hi_mmiss = new H1F("hi_mmiss","hi_mmiss",200,2.8,3.4);        
        hi_mmiss.setTitleX("Mx(ep#rarrow e'p'X) (GeV)");
        hi_mmiss.setTitleY("Counts");
        hi_mmiss.setOptStat("1110");
        F1D f1_mmiss = new F1D("f1_mmiss","[amp]*gaus(x,[mean],[sigma])", 2.8, 3.4);
        f1_mmiss.setParameter(0, 0);
        f1_mmiss.setParameter(1, 3.1);
        f1_mmiss.setParameter(2, 0.01);
        f1_mmiss.setLineWidth(3);
        f1_mmiss.setLineColor(4);
        f1_mmiss.setOptStat("1111");
        H1F hi_mm_mres = new H1F("hi_mm_mres","hi_mm_mres",200, -0.1, 0.1);        
        hi_mm_mres.setTitleX("#DeltaM(p'l+l-) (GeV)");
        hi_mm_mres.setTitleY("Counts");
        hi_mm_mres.setOptStat("1110");    
        F1D f1_mm_mres = new F1D("f1_mm_mres","[amp]*gaus(x,[mean],[sigma])", -0.1, 0.1);
        f1_mm_mres.setParameter(0, 0);
        f1_mm_mres.setParameter(1, 0);
        f1_mm_mres.setParameter(2, 0.01);
        f1_mm_mres.setLineWidth(3);
        f1_mm_mres.setLineColor(4);
        f1_mm_mres.setOptStat("1111");
        H1F hi_mm_w = new H1F("hi_mm_w","hi_mm_w",100, 4., 4.8); 
        hi_mm_w.setTitleX("W (GeV)");
        hi_mm_w.setTitleY("Counts");
        hi_mm_w.setOptStat("1110");
        H2F hi_mm_ptheta = new H2F("hi_mm_ptheta","hi_mm_ptheta",100, 0., 6., 100, 0., 35.);
        hi_mm_ptheta.setTitleX("Pp (GeV)");
        hi_mm_ptheta.setTitleY("#thetap (deg)");
        H1F hi_mmiss_e = new H1F("hi_mmiss_e","hi_mmiss_e",200, -0.5, 0.5);        
        hi_mmiss_e.setTitleX("Mx(ep#rarrow e'p'lX) (GeV)");
        hi_mmiss_e.setTitleY("Counts");
        hi_mmiss_e.setOptStat("1110");
        H2F hi_mm_mresvsm = new H2F("hi_mm_mresvsm","hi_mm_mresvsm",100, 4., 4.8, 100, -0.1, 0.1);        
        hi_mm_mresvsm.setTitleX("M(p'l+l-) (GeV)");
        hi_mm_mresvsm.setTitleY("#DeltaM(p'l+l-) (GeV)");
        DataGroup dg_missing = new DataGroup(4,2);
        dg_missing.addDataSet(hi_mm_eg, 0);
        dg_missing.addDataSet(hi_mm_q2w, 1);
        dg_missing.addDataSet(hi_mmiss, 2);
        dg_missing.addDataSet(f1_mmiss, 2);
        dg_missing.addDataSet(hi_mm_mres, 3);
        dg_missing.addDataSet(f1_mm_mres, 3);
        dg_missing.addDataSet(hi_mm_w, 4);
        dg_missing.addDataSet(hi_mm_ptheta, 5);
        dg_missing.addDataSet(hi_mmiss_e, 6);
        dg_missing.addDataSet(hi_mm_mresvsm, 7);
        this.getDataGroup().add(dg_missing, 5);
        // electrons
        H1F hi_dE_EC = new H1F("hi_dE_EC", "hi_dE_EC", 100, -0.8, 0.8);   
        hi_dE_EC.setTitleX("E-p (GeV)");
        hi_dE_EC.setTitleY("Counts");
        H1F hi_sf_EC = new H1F("hi_sf_EC", "hi_sf_EC", 100,  0.0, 0.6);   
        hi_sf_EC.setTitleX("E/p");
        hi_sf_EC.setTitleY("Counts");
        F1D f1_sf = new F1D("f1_sf","[amp]*gaus(x,[mean],[sigma])", 0.20, 0.30);
        f1_sf.setParameter(0, 0);
        f1_sf.setParameter(1, 0.245);
        f1_sf.setParameter(2, 0.1);
        f1_sf.setLineWidth(3);
        f1_sf.setLineColor(4);
        f1_sf.setOptStat("1111");
        H2F hi_Evsp_EC = new H2F("hi_Evsp_EC", "hi_Evsp_EC", 100, 0, 6, 100, 0, 6);  
        hi_Evsp_EC.setTitleX("p (GeV)"); 
        hi_Evsp_EC.setTitleY("E (GeV)");
        H2F hi_sfvsp_EC = new H2F("hi_sfvsp_EC", "hi_sfvsp_EC", 100, 0, 6, 100, 0, 0.6);  
        hi_sfvsp_EC.setTitleX("p (GeV)"); 
        hi_sfvsp_EC.setTitleY("E/p");       
        H2F hi_ECin_vs_PCAL = new H2F("hi_ECin_vs_PCAL", "hi_ECin_vs_PCAL", 100, 0, 1, 100, 0, 0.8);  
        hi_ECin_vs_PCAL.setTitleX("PCAL energy (GeV)"); 
        hi_ECin_vs_PCAL.setTitleY("EC energy (GeV)");
//        hi_ECin_vs_PCAL.
        DataGroup dg_electron = new DataGroup(1,5);
        dg_electron.addDataSet(hi_dE_EC, 0);
        dg_electron.addDataSet(hi_Evsp_EC, 1);
        dg_electron.addDataSet(hi_sf_EC, 2);
        dg_electron.addDataSet(f1_sf, 2);
        dg_electron.addDataSet(hi_sfvsp_EC, 3);
        dg_electron.addDataSet(hi_ECin_vs_PCAL, 3);
        this.getDataGroup().add(dg_electron, 6);
        // protons
        H1F hi_E_pr = new H1F("hi_E_pr", "hi_E_pr", 100, 0., 1.);   
        hi_E_pr.setTitleX("E (GeV)");
        hi_E_pr.setTitleY("Counts");
        hi_E_pr.setOptStat("1110");
        H2F hi_Evsp_pr = new H2F("hi_Evsp_pr", "hi_Evsp_pr", 100, 0., 6., 100, 0., 1.);   
        hi_Evsp_pr.setTitleX("p (GeV)");
        hi_Evsp_pr.setTitleY("E (GeV)");
        H1F hi_T_pr = new H1F("hi_T_pr", "hi_T_pr", 100, -10., 120.);   
        hi_T_pr.setTitleX("TOF (ns)");
        hi_T_pr.setTitleY("Counts");
        hi_T_pr.setOptStat("1110");
        H2F hi_betavsp_pr = new H2F("hi_betavsp_pr", "hi_betavsp_pr", 100, 0., 6., 100, 0., 1.2);   
        hi_betavsp_pr.setTitleX("p (GeV)");
        hi_betavsp_pr.setTitleY("beta");
        DataGroup dg_proton = new DataGroup(2,2);
        dg_proton.addDataSet(hi_E_pr, 0);
        dg_proton.addDataSet(hi_Evsp_pr, 1);
        dg_proton.addDataSet(hi_T_pr, 2);
        dg_proton.addDataSet(hi_betavsp_pr, 3);
        this.getDataGroup().add(dg_proton, 7);
       

    }
    
    @Override
    public void plotHistos() {
        this.getAnalysisCanvas().getCanvas("Generated").divide(3, 2);
        this.getAnalysisCanvas().getCanvas("Generated").setGridX(false);
        this.getAnalysisCanvas().getCanvas("Generated").setGridY(false);
        this.getAnalysisCanvas().getCanvas("Generated").setTitleSize(24);
        this.getAnalysisCanvas().getCanvas("Reconstructed").divide(2, 2);
        this.getAnalysisCanvas().getCanvas("Reconstructed").setGridX(false);
        this.getAnalysisCanvas().getCanvas("Reconstructed").setGridY(false);
        this.getAnalysisCanvas().getCanvas("Reconstructed").setTitleSize(24);
        this.getAnalysisCanvas().getCanvas("Resolution").divide(2, 2);
        this.getAnalysisCanvas().getCanvas("Resolution").setGridX(false);
        this.getAnalysisCanvas().getCanvas("Resolution").setGridY(false);
        this.getAnalysisCanvas().getCanvas("Resolution").setTitleSize(24);
        this.getAnalysisCanvas().getCanvas("Electrons").divide(2, 2);
        this.getAnalysisCanvas().getCanvas("Electrons").setGridX(false);
        this.getAnalysisCanvas().getCanvas("Electrons").setGridY(false);
        this.getAnalysisCanvas().getCanvas("Invariant Mass").divide(4, 2);
        this.getAnalysisCanvas().getCanvas("Invariant Mass").setGridX(false);
        this.getAnalysisCanvas().getCanvas("Invariant Mass").setGridY(false);
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").divide(4, 2);
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").setGridX(false);
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").setGridY(false);
        this.getAnalysisCanvas().getCanvas("Protons").divide(2, 2);
        this.getAnalysisCanvas().getCanvas("Protons").setGridX(false);
        this.getAnalysisCanvas().getCanvas("Protons").setGridY(false);
        
        this.getAnalysisCanvas().getCanvas("Generated").cd(0);
        this.getAnalysisCanvas().getCanvas("Generated").draw(this.getDataGroup().getItem(1).getH1F("hi_gen_w"));
        this.getAnalysisCanvas().getCanvas("Generated").cd(1);
        this.getAnalysisCanvas().getCanvas("Generated").getPad(1).getAxisZ().setLog(true);
        this.getAnalysisCanvas().getCanvas("Generated").draw(this.getDataGroup().getItem(1).getH2F("hi_gen_q2w"));
        this.getAnalysisCanvas().getCanvas("Generated").cd(2);
        this.getAnalysisCanvas().getCanvas("Generated").getPad(2).getAxisZ().setLog(true);
        this.getAnalysisCanvas().getCanvas("Generated").draw(this.getDataGroup().getItem(1).getH2F("hi_gen_el"));
        this.getAnalysisCanvas().getCanvas("Generated").cd(3);
        this.getAnalysisCanvas().getCanvas("Generated").draw(this.getDataGroup().getItem(1).getH2F("hi_gen_pr"));
        this.getAnalysisCanvas().getCanvas("Generated").cd(4);
        this.getAnalysisCanvas().getCanvas("Generated").draw(this.getDataGroup().getItem(1).getH2F("hi_gen_ep"));
        this.getAnalysisCanvas().getCanvas("Generated").cd(5);
        this.getAnalysisCanvas().getCanvas("Generated").draw(this.getDataGroup().getItem(1).getH2F("hi_gen_em"));
        
        this.getAnalysisCanvas().getCanvas("Reconstructed").cd(0);
        this.getAnalysisCanvas().getCanvas("Reconstructed").draw(this.getDataGroup().getItem(2).getH2F("hi_rec_el"));
        this.getAnalysisCanvas().getCanvas("Reconstructed").cd(1);
        this.getAnalysisCanvas().getCanvas("Reconstructed").draw(this.getDataGroup().getItem(2).getH2F("hi_rec_pr"));
        this.getAnalysisCanvas().getCanvas("Reconstructed").cd(2);
        this.getAnalysisCanvas().getCanvas("Reconstructed").draw(this.getDataGroup().getItem(2).getH2F("hi_rec_ep"));
        this.getAnalysisCanvas().getCanvas("Reconstructed").cd(3);
        this.getAnalysisCanvas().getCanvas("Reconstructed").draw(this.getDataGroup().getItem(2).getH2F("hi_rec_em"));
        
        this.getAnalysisCanvas().getCanvas("Resolution").cd(0);
        this.getAnalysisCanvas().getCanvas("Resolution").draw(this.getDataGroup().getItem(3).getH1F("hi_res_el"));
        this.getAnalysisCanvas().getCanvas("Resolution").cd(1);
        this.getAnalysisCanvas().getCanvas("Resolution").draw(this.getDataGroup().getItem(3).getH1F("hi_res_pr"));
        this.getAnalysisCanvas().getCanvas("Resolution").cd(2);
        this.getAnalysisCanvas().getCanvas("Resolution").draw(this.getDataGroup().getItem(3).getH1F("hi_res_ep"));
        this.getAnalysisCanvas().getCanvas("Resolution").cd(3);
        this.getAnalysisCanvas().getCanvas("Resolution").draw(this.getDataGroup().getItem(3).getH1F("hi_res_em"));

        this.getAnalysisCanvas().getCanvas("Invariant Mass").cd(0);
        this.getAnalysisCanvas().getCanvas("Invariant Mass").draw(this.getDataGroup().getItem(4).getH1F("hi_im_eg"));
        this.getAnalysisCanvas().getCanvas("Invariant Mass").cd(1);
        this.getAnalysisCanvas().getCanvas("Invariant Mass").draw(this.getDataGroup().getItem(4).getH1F("hi_im_w"));
        this.getAnalysisCanvas().getCanvas("Invariant Mass").cd(2);
        this.getAnalysisCanvas().getCanvas("Invariant Mass").draw(this.getDataGroup().getItem(4).getH1F("hi_mepem"));
        this.getAnalysisCanvas().getCanvas("Invariant Mass").draw(this.getDataGroup().getItem(4).getF1D("f1_mepem"),"same");
        this.getAnalysisCanvas().getCanvas("Invariant Mass").cd(3);
        this.getAnalysisCanvas().getCanvas("Invariant Mass").draw(this.getDataGroup().getItem(4).getH1F("hi_im_mres"));
        this.getAnalysisCanvas().getCanvas("Invariant Mass").draw(this.getDataGroup().getItem(4).getF1D("f1_im_mres"),"same");
        this.getAnalysisCanvas().getCanvas("Invariant Mass").cd(4);
        this.getAnalysisCanvas().getCanvas("Invariant Mass").draw(this.getDataGroup().getItem(4).getH1F("hi_im_eg_acc"));
        this.getAnalysisCanvas().getCanvas("Invariant Mass").cd(5);
        this.getAnalysisCanvas().getCanvas("Invariant Mass").getPad(1).getAxisZ().setLog(true);
        this.getAnalysisCanvas().getCanvas("Invariant Mass").draw(this.getDataGroup().getItem(4).getH2F("hi_im_q2w"));
        this.getAnalysisCanvas().getCanvas("Invariant Mass").cd(6);
        this.getAnalysisCanvas().getCanvas("Invariant Mass").draw(this.getDataGroup().getItem(4).getH1F("hi_im_emiss"));
        this.getAnalysisCanvas().getCanvas("Invariant Mass").cd(7);
        this.getAnalysisCanvas().getCanvas("Invariant Mass").draw(this.getDataGroup().getItem(4).getH2F("hi_im_mresvsm"));

        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").cd(0);
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").draw(this.getDataGroup().getItem(5).getH1F("hi_mm_eg"));
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").cd(1);
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").draw(this.getDataGroup().getItem(5).getH2F("hi_mm_q2w"));
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").cd(2);
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").draw(this.getDataGroup().getItem(5).getH1F("hi_mmiss"));
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").draw(this.getDataGroup().getItem(5).getF1D("f1_mmiss"),"same");
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").cd(3);
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").draw(this.getDataGroup().getItem(5).getH1F("hi_mm_mres"));
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").draw(this.getDataGroup().getItem(5).getF1D("f1_mm_mres"),"same");
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").cd(4);
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").draw(this.getDataGroup().getItem(5).getH1F("hi_mm_w"));
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").cd(5);        
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").draw(this.getDataGroup().getItem(5).getH2F("hi_mm_ptheta"));
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").cd(6);        
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").draw(this.getDataGroup().getItem(5).getH1F("hi_mmiss_e"));
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").cd(7);
        this.getAnalysisCanvas().getCanvas("Missing Mass (FT)").draw(this.getDataGroup().getItem(5).getH2F("hi_mm_mresvsm"));
        
        this.getAnalysisCanvas().getCanvas("Electrons").cd(0);
//        this.getAnalysisCanvas().getCanvas("Electrons").draw(this.getDataGroup().getItem(1).getH1F("hi_dE_EC"));
        this.getAnalysisCanvas().getCanvas("Electrons").getPad(0).getAxisZ().setLog(true);
        this.getAnalysisCanvas().getCanvas("Electrons").draw(this.getDataGroup().getItem(6).getH2F("hi_ECin_vs_PCAL"));
        this.getAnalysisCanvas().getCanvas("Electrons").cd(1);
        this.getAnalysisCanvas().getCanvas("Electrons").draw(this.getDataGroup().getItem(6).getH1F("hi_sf_EC"));
        this.getAnalysisCanvas().getCanvas("Electrons").draw(this.getDataGroup().getItem(6).getF1D("f1_sf"),"same");
        this.getAnalysisCanvas().getCanvas("Electrons").cd(2);
        this.getAnalysisCanvas().getCanvas("Electrons").getPad(2).getAxisZ().setLog(true);
        this.getAnalysisCanvas().getCanvas("Electrons").draw(this.getDataGroup().getItem(6).getH2F("hi_Evsp_EC"));
        this.getAnalysisCanvas().getCanvas("Electrons").cd(3);
        this.getAnalysisCanvas().getCanvas("Electrons").getPad(3).getAxisZ().setLog(true);
        this.getAnalysisCanvas().getCanvas("Electrons").draw(this.getDataGroup().getItem(6).getH2F("hi_sfvsp_EC"));
               
        this.getAnalysisCanvas().getCanvas("Protons").cd(0);
//        this.getAnalysisCanvas().getCanvas("Electrons").draw(this.getDataGroup().getItem(1).getH1F("hi_dE_EC"));
        this.getAnalysisCanvas().getCanvas("Protons").getPad(0).getAxisZ().setLog(true);
        this.getAnalysisCanvas().getCanvas("Protons").draw(this.getDataGroup().getItem(7).getH1F("hi_E_pr"));
        this.getAnalysisCanvas().getCanvas("Protons").cd(1);
        this.getAnalysisCanvas().getCanvas("Protons").draw(this.getDataGroup().getItem(7).getH2F("hi_Evsp_pr"));
        this.getAnalysisCanvas().getCanvas("Protons").cd(2);
        this.getAnalysisCanvas().getCanvas("Protons").getPad(2).getAxisZ().setLog(true);
        this.getAnalysisCanvas().getCanvas("Protons").draw(this.getDataGroup().getItem(7).getH1F("hi_T_pr"));
        this.getAnalysisCanvas().getCanvas("Protons").cd(3);
        this.getAnalysisCanvas().getCanvas("Protons").getPad(3).getAxisZ().setLog(true);
        this.getAnalysisCanvas().getCanvas("Protons").draw(this.getDataGroup().getItem(7).getH2F("hi_betavsp_pr"));
    }
        
    @Override
    public void processEvent(DataEvent event) {
        // process event info and save into data group   
        nEvent++;
//        System.out.println("Analyzed " + nEvent + " events and found " + nGen + " MC events, " + nElec + " electrons, " + nIM + "/" + nMM + " masses");
        Particle partRecEB = null;
        // event builder
        DataBank recBankEB = null;
        DataBank recDeteEB = null;
        DataBank recBankTB = null;
        DataBank recBankFT = null;
        DataBank genBankMC = null;
        Particle genEl = null;
        Particle genEp = null;
        Particle genEm = null;
        Particle genPr = null;
        Particle recEl = null;
        Particle recEp = null;
        Particle recEm = null;
        Particle recPr = null;
        Particle recFT = null;
        if(event.hasBank("REC::Particle")) recBankEB = event.getBank("REC::Particle");
        if(event.hasBank("REC::Detector")) recDeteEB = event.getBank("REC::Detector");
        if(event.hasBank("TimeBasedTrkg::TBTracks")) recBankTB = event.getBank("TimeBasedTrkg::TBTracks");
        if(event.hasBank("FT::particles")) recBankFT = event.getBank("FT::particles");
        if(event.hasBank("MC::Particle"))  genBankMC = event.getBank("MC::Particle");
        if(genBankMC!=null) {
            int nrows = genBankMC.rows();
            for(int loop = 0; loop < nrows; loop++) {   
                Particle genPart = new Particle(
                                              genBankMC.getInt("pid", loop),
                                              genBankMC.getFloat("px", loop),
                                              genBankMC.getFloat("py", loop),
                                              genBankMC.getFloat("pz", loop),
                                              genBankMC.getFloat("vx", loop),
                                              genBankMC.getFloat("vy", loop),
                                              genBankMC.getFloat("vz", loop));
                if(loop==0) genEl=genPart;
                else {
                    if(genPart.pid()==2212) genPr=genPart;
                    if(genPart.pid()==-11)   genEp=genPart;
                    if(genPart.pid()==11)  genEm=genPart;
                    if(genPart.pid()==-13)   genEp=genPart;
                    if(genPart.pid()==13)  genEm=genPart;
                }
            }
            LorentzVector virtualPhoton = new LorentzVector(0., 0., 11., 11.);
            virtualPhoton.sub(genEl.vector());
            LorentzVector hadronSystem = new LorentzVector(0., 0., 11., 0.9383+11.);
            hadronSystem.sub(genEl.vector());
            this.getDataGroup().getItem(1).getH2F("hi_gen_q2w").fill(hadronSystem.mass(),-virtualPhoton.mass2());
            this.getDataGroup().getItem(1).getH1F("hi_gen_w").fill(hadronSystem.mass());
            this.getDataGroup().getItem(1).getH1F("hi_gen_eg").fill(virtualPhoton.e());
            nGen++;
        }
        if(recBankEB!=null && recDeteEB!=null) {
            int nrows = recBankEB.rows();
            for(int loop = 0; loop < nrows; loop++){
                int pidCode = 0;
                if(recBankEB.getInt("pid", loop)!=0) pidCode = recBankEB.getInt("pid", loop);
                else if(recBankEB.getByte("charge", loop)==-1) pidCode = 11;
                else if(recBankEB.getByte("charge", loop)==1) pidCode = -11;
                else pidCode = 2112;
                Particle recParticle = new Particle(
                                            pidCode,
                                            recBankEB.getFloat("px", loop),
                                            recBankEB.getFloat("py", loop),
                                            recBankEB.getFloat("pz", loop),
                                            recBankEB.getFloat("vx", loop),
                                            recBankEB.getFloat("vy", loop),
                                            recBankEB.getFloat("vz", loop));
                double energy1=0;
                double energy4=0;
                double energy7=0;
                double time=0;
                double path=0;
                for(int j=0; j<recDeteEB.rows(); j++) {
                    if(recDeteEB.getShort("pindex",j)==loop && recDeteEB.getShort("detector",j)==16) {
                        if(energy1 >= 0 && recDeteEB.getShort("layer",j) == 1) energy1 += recDeteEB.getFloat("energy",j);
                        if(energy4 >= 0 && recDeteEB.getShort("layer",j) == 4) energy4 += recDeteEB.getFloat("energy",j);
                        if(energy7 >= 0 && recDeteEB.getShort("layer",j) == 7) energy7 += recDeteEB.getFloat("energy",j);
                    }
                } 
                for(int j=0; j<recDeteEB.rows(); j++) {
                    if(recDeteEB.getShort("pindex",j)==loop && recDeteEB.getShort("detector",j)==17 && recDeteEB.getShort("layer",j) == 2) {
                        time = recDeteEB.getFloat("time",j);
                        path = recDeteEB.getFloat("path",j);
                    }
                }
                recParticle.setProperty("energy1",energy1);
                recParticle.setProperty("energy4",energy4);
                recParticle.setProperty("energy7",energy7);
                recParticle.setProperty("time",time);
                recParticle.setProperty("path",path);
//                if(recBankTB!=null) recParticle.setProperty("sector",recBankTB.getByte("sector", loop)*1.0);
                if(energy1>0.0 && energy4>0 && Math.abs(recParticle.vz()-2)<2. && recParticle.charge()==-1) {
                    double energy=(energy1+energy4+energy7)/0.245;
                    this.getDataGroup().getItem(6).getH2F("hi_ECin_vs_PCAL").fill(energy1,energy4+energy7);
                    if(energy1>0.1 && energy4>0.0 && recParticle.p()>0) {
                        this.getDataGroup().getItem(6).getH2F("hi_Evsp_EC").fill(recParticle.p(),energy);
                        this.getDataGroup().getItem(6).getH2F("hi_sfvsp_EC").fill(recParticle.p(),(energy1+energy4+energy7)/recParticle.p());
                        this.getDataGroup().getItem(6).getH1F("hi_dE_EC").fill(energy-recParticle.p());
                        this.getDataGroup().getItem(6).getH1F("hi_sf_EC").fill((energy1+energy4+energy7)/recParticle.p());
                    }
                }
                if(recParticle.charge()==-1) {
                    if(this.momentumCheck(recParticle, genEl, 0.1))      recEl = recParticle;
                    else if(this.momentumCheck(recParticle, genEm, 0.1)) {
                        recEm = recParticle;
                        recEm.changePid(genEm.pid());
                    }
                }
                else if(recParticle.charge()==1) {
                    if(this.momentumCheck(recParticle, genEp, 0.1))      {
                        recEp = recParticle;
                        recEp.changePid(genEp.pid());
                    }
                    else if(this.momentumCheck(recParticle, genPr, 0.1)) {
                        recParticle.changePid(2212);
                        recPr = recParticle;
                    }
                } 
            }
            if(recPr != null) {
                double energy = recPr.getProperty("energy1") + recPr.getProperty("energy4") + recPr.getProperty("energy7");
                this.getDataGroup().getItem(7).getH1F("hi_E_pr").fill(energy);
                this.getDataGroup().getItem(7).getH2F("hi_Evsp_pr").fill(recPr.p(),energy);
                double beta = 0;
                double time = recPr.getProperty("time");
                if(time>124.5) beta=recPr.getProperty("path")/(time-124.5)/29.97;
                this.getDataGroup().getItem(7).getH1F("hi_T_pr").fill(time-124.5);
                this.getDataGroup().getItem(7).getH2F("hi_betavsp_pr").fill(recPr.p(),beta);
            }
        }
        if(recBankFT != null) {
            int nrows = recBankFT.rows();
            for(int loop = 0; loop < nrows; loop++){
                if(recBankFT.getByte("charge",loop)==-1) {
                    Particle recParticle = new Particle(
                                            11,
                                            recBankFT.getFloat("cx", loop)*recBankFT.getFloat("energy", loop),
                                            recBankFT.getFloat("cy", loop)*recBankFT.getFloat("energy", loop),
                                            recBankFT.getFloat("cz", loop)*recBankFT.getFloat("energy", loop),
                                            0,0,0);
                    if(recEl == null || true) {
                        if(this.momentumCheck(recParticle, genEl,0.2)) {
                            recFT = recParticle;
                            recEl = recParticle;
                            nElec++;
                        }
                    }
                }
            }
        }
//                genBankMC.show();
//                recBankEB.show();
        if(genEl != null) this.getDataGroup().getItem(1).getH2F("hi_gen_el").fill(genEl.p(),Math.toDegrees(genEl.theta()));
        if(genPr != null) this.getDataGroup().getItem(1).getH2F("hi_gen_pr").fill(genPr.p(),Math.toDegrees(genPr.theta()));
        if(genEp != null) this.getDataGroup().getItem(1).getH2F("hi_gen_ep").fill(genEp.p(),Math.toDegrees(genEp.theta()));
        if(genEm != null) this.getDataGroup().getItem(1).getH2F("hi_gen_em").fill(genEm.p(),Math.toDegrees(genEm.theta()));
        if(recEl != null) this.getDataGroup().getItem(2).getH2F("hi_rec_el").fill(recEl.p(),Math.toDegrees(recEl.theta()));
        if(recPr != null) this.getDataGroup().getItem(2).getH2F("hi_rec_pr").fill(recPr.p(),Math.toDegrees(recPr.theta()));
        if(recEp != null) this.getDataGroup().getItem(2).getH2F("hi_rec_ep").fill(recEp.p(),Math.toDegrees(recEp.theta()));
        if(recEm != null) this.getDataGroup().getItem(2).getH2F("hi_rec_em").fill(recEm.p(),Math.toDegrees(recEm.theta()));
        if(genEl != null && recEl != null) this.getDataGroup().getItem(3).getH1F("hi_res_el").fill(recEl.p()-genEl.p());
        if(genPr != null && recPr != null) this.getDataGroup().getItem(3).getH1F("hi_res_pr").fill(recPr.p()-genPr.p());
        if(genEp != null && recEp != null) this.getDataGroup().getItem(3).getH1F("hi_res_ep").fill(recEp.p()-genEp.p());
        if(genEm != null && recEm != null) this.getDataGroup().getItem(3).getH1F("hi_res_em").fill(recEm.p()-genEm.p());
        if(recEp != null && recEm != null) nIM_epem++;
        if(recEp != null && recEm != null && recPr != null) {
            LorentzVector invJpsi = new LorentzVector();
            invJpsi.copy(recEm.vector());
            invJpsi.add(recEp.vector());
            LorentzVector hadronSystem = new LorentzVector();
            hadronSystem.copy(recPr.vector());
            hadronSystem.add(recEp.vector());
            hadronSystem.add(recEm.vector());
            LorentzVector virtualPhoton = new LorentzVector(0., 0., 0., -0.9383);
            virtualPhoton.add(hadronSystem);
            LorentzVector mmLepton = new LorentzVector(0., 0., 11.,0.9393+11.);
            mmLepton.sub(hadronSystem);
            LorentzVector genHadronSystem = new LorentzVector();
            genHadronSystem.copy(genPr.vector());
            genHadronSystem.add(genEp.vector());
            genHadronSystem.add(genEm.vector());
            this.getDataGroup().getItem(4).getH1F("hi_mepem").fill(invJpsi.mass()); 
            this.getDataGroup().getItem(4).getH2F("hi_im_q2w").fill(hadronSystem.mass(),-virtualPhoton.mass2());
            this.getDataGroup().getItem(4).getH1F("hi_im_w").fill(hadronSystem.mass());
            this.getDataGroup().getItem(4).getH1F("hi_im_eg").fill(virtualPhoton.e());
            this.getDataGroup().getItem(4).getH1F("hi_im_emiss").fill(mmLepton.mass2());
            this.getDataGroup().getItem(4).getH1F("hi_im_mres").fill(hadronSystem.mass()-genHadronSystem.mass());
            this.getDataGroup().getItem(4).getH2F("hi_im_mresvsm").fill(genHadronSystem.mass(),hadronSystem.mass()-genHadronSystem.mass());
            nIM++;
        }
        if(recEl != null && recEp != null && recEm != null) nMM_epem++;
        if(recEl != null && recPr != null) {
            LorentzVector mmJpsi = new LorentzVector(0., 0., 11., 0.9393+11.);
            mmJpsi.sub(recEl.vector());
            mmJpsi.sub(recPr.vector());
            LorentzVector virtualPhoton = new LorentzVector(0., 0., 11., 11.);
            virtualPhoton.sub(recEl.vector());
            LorentzVector hadronSystem = new LorentzVector(0., 0., 11., 0.9383+11.);
            hadronSystem.sub(recEl.vector());
            LorentzVector genHadronSystem = new LorentzVector(0., 0., 11., 0.9383+11.);
            genHadronSystem.sub(genEl.vector());
            if(recEp != null || recEm != null) {
                LorentzVector mmLepton = new LorentzVector(0., 0., 11.,0.9393+11.);
                mmLepton.sub(recEl.vector());
                mmLepton.sub(recPr.vector());   
                if(recEp != null) mmLepton.sub(recEp.vector()); 
                else              mmLepton.sub(recEm.vector());
                this.getDataGroup().getItem(5).getH1F("hi_mmiss_e").fill(mmLepton.mass2());
                if(recEp != null) nMM_ep++;
                if(recEm != null) nMM_em++;
                if(recEp != null && recEm != null) nMM_pepem++;
            }
            this.getDataGroup().getItem(5).getH1F("hi_mmiss").fill(mmJpsi.mass());
            this.getDataGroup().getItem(5).getH1F("hi_mm_eg").fill(virtualPhoton.e());
            this.getDataGroup().getItem(5).getH1F("hi_mm_w").fill(hadronSystem.mass());
            this.getDataGroup().getItem(5).getH2F("hi_mm_q2w").fill(hadronSystem.mass(),-virtualPhoton.mass2());
            this.getDataGroup().getItem(5).getH2F("hi_mm_ptheta").fill(recPr.p(),Math.toDegrees(recPr.theta()));
            this.getDataGroup().getItem(5).getH1F("hi_mm_mres").fill(hadronSystem.mass()-genHadronSystem.mass());
            this.getDataGroup().getItem(5).getH2F("hi_mm_mresvsm").fill(genHadronSystem.mass(),hadronSystem.mass()-genHadronSystem.mass());
            nMM++;
        }
        if(nEvent % 10000 == 0) System.out.println("Analyzed " + nEvent + " events and found " + nGen + " MC events, " + nElec + " electrons in the FT, " 
                + nIM + "/" + nMM + " IM/MM masses, " + nIM_epem + " e+e- events, " + nMM_ep + "/" + nMM_em + " e'pe+/e- events, " 
                + nMM_epem + " e'e+/e- events, " + nMM_pepem + " e'pe+e- events" );
    }
    
    private boolean momentumCheck(Particle p1, Particle p2, double dp) {
        boolean flag = false;
        if(Math.abs(p1.px()-p2.px())<dp &&
           Math.abs(p1.py()-p2.py())<dp &&
           Math.abs(p1.pz()-p2.pz())<dp) flag = true; 
        return flag;
    }
    
    @Override
    public void analyze() {
        if(nEvent % 10000 == 0) System.out.println("Analyzed " + nEvent + " events and found " + nGen + " MC events, " + nElec + " electrons in the FT, " 
                + nIM + "/" + nMM + " IM/MM masses, " + nIM_epem + " e+e- events, " + nMM_ep + "/" + nMM_em + " e'pe+/e- events, " 
                + nMM_epem + " e'e+/e- events, " + nMM_pepem + " e'pe+e- events" );
    }

    @Override
    public void timerUpdate() {
        // fitting sampling fraction
        this.fitGauss(this.getDataGroup().getItem(4).getH1F("hi_mepem"),this.getDataGroup().getItem(4).getF1D("f1_mepem"));
        this.fitGauss(this.getDataGroup().getItem(4).getH1F("hi_im_mres"),this.getDataGroup().getItem(4).getF1D("f1_im_mres"));
        this.fitGauss(this.getDataGroup().getItem(5).getH1F("hi_mmiss"),this.getDataGroup().getItem(5).getF1D("f1_mmiss"));
        this.fitGauss(this.getDataGroup().getItem(5).getH1F("hi_mm_mres"),this.getDataGroup().getItem(5).getF1D("f1_mm_mres"));
//        this.fitGauss(this.getDataGroup().getItem(6).getH1F("hi_sf_EC"),this.getDataGroup().getItem(6).getF1D("f1_sf"));
        this.getAcceptance(this.getDataGroup().getItem(1).getH1F("hi_gen_eg"), this.getDataGroup().getItem(4).getH1F("hi_im_eg"), this.getDataGroup().getItem(4).getH1F("hi_im_eg_acc"));
    }

    private void fitGauss(H1F hi, F1D f1) {
        f1.setParameter(0, hi.getBinContent(hi.getMaximumBin()));
        f1.setParameter(1, hi.getDataX(hi.getMaximumBin()));
        f1.setParameter(2, hi.getRMS()/5.);
        DataFitter.fit(f1,hi,"Q");
    }

    private void getAcceptance(H1F h1, H1F h2, H1F h3) {
        if(h1.getXaxis().getNBins()==h2.getXaxis().getNBins() && h1.getXaxis().getNBins()==h3.getXaxis().getNBins()) {
            for(int i=0; i<h1.getXaxis().getNBins(); i++) {
                double ngen=h1.getBinContent(i);
                double nrec=h2.getBinContent(i);
                if(ngen>0) h3.setBinContent(i, 100*nrec/ngen);
            }
        }
    }
}
