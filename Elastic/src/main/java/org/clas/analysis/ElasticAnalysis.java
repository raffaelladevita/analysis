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
import org.jlab.detector.base.DetectorType;
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
public class ElasticAnalysis extends AnalysisMonitor {
    
    private int nEvent, nGen, nElec, nPr;
//    private double ebeam = 10.6;
//    private double xsec  = 0.031301651 ;
    private double ebeam = 6.4;
    private double xsec  = 0.17659394;
//    private double ebeam = 4.3;
//    private double xsec  = 0.69260705 ;
//    private double ebeam = 2.1;
//    private double xsec  = 5.3980818 ;
    private double trun  = 1;
    private boolean ft;
    private double scale = 1/1.;

    public ElasticAnalysis(String name) {
        super(name);
        this.setAnalysisTabNames("Generated", "Reconstructed", "Resolution","Electrons", "Protons");
        this.init(false);
        
        trun     = 1.0/100000./xsec;
        nEvent   = 0;
        nGen     = 0;
        nElec    = 0;
        nPr      = 0;
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
        H2F hi_gen_q2w = new H2F("hi_gen_q2w","hi_gen_q2w",100, 0.6, ebeam*0.6+0.6, 100, 0., 3.); 
        hi_gen_q2w.setTitleX("W (GeV)");
        hi_gen_q2w.setTitleY("Q2 (GeV2)");
        H1F hi_gen_w = new H1F("hi_gen_w","hi_gen_w",250, 0.6, ebeam*0.6+0.6); 
        hi_gen_w.setTitleX("W (GeV)");
        hi_gen_w.setTitleY("Counts");
        hi_gen_w.setOptStat("1110"); 
        H2F hi_gen_el = new H2F("hi_gen_el","hi_gen_el",100, 0., ebeam+0.6, 100, 0., 50.);
        hi_gen_el.setTitleX("p (GeV)");
        hi_gen_el.setTitleY("#theta (deg)");
        hi_gen_el.setTitle("Electron");
        H2F hi_gen_pr = new H2F("hi_gen_pr","hi_gen_pr",100, 0., ebeam, 100, 0., 90.);
        hi_gen_pr.setTitleX("p (GeV)");
        hi_gen_pr.setTitleY("#theta (deg)");
        hi_gen_pr.setTitle("Proton");
        H1F hi_gen_rate = new H1F("hi_gen_rate","hi_gen_rate",250, 0.6, ebeam*0.6+0.6); 
        hi_gen_rate.setTitleX("W (GeV)");
        hi_gen_rate.setTitleY("Rate (Hz)");
        hi_gen_rate.setOptStat("1110"); 
        DataGroup dg_generated = new DataGroup(2,3);
        dg_generated.addDataSet(hi_gen_w,  0);
        dg_generated.addDataSet(hi_gen_q2w, 1);
        dg_generated.addDataSet(hi_gen_el, 2);
        dg_generated.addDataSet(hi_gen_pr, 3);        
        dg_generated.addDataSet(hi_gen_rate, 4);
        this.getDataGroup().add(dg_generated, 1);
        // Reconstructed
        H2F hi_rec_q2w = new H2F("hi_rec_q2w","hi_rec_q2w",100, 0.6, ebeam*0.6+0.6, 100, 0., 3.); 
        hi_rec_q2w.setTitleX("W (GeV)");
        hi_rec_q2w.setTitleY("Q2 (GeV2)");
        H1F hi_rec_w = new H1F("hi_rec_w","hi_rec_w",250, 0.6, ebeam*0.6+0.6); 
        hi_rec_w.setTitleX("W (GeV)");
        hi_rec_w.setTitleY("Counts");
        hi_rec_w.setOptStat("1110"); 
        H2F hi_rec_el = new H2F("hi_rec_el","hi_rec_el",100, 0., ebeam+1.6, 100, 0., 50.);
        hi_rec_el.setTitleX("p (GeV)");
        hi_rec_el.setTitleY("#theta (deg)");
        hi_rec_el.setTitle("Electron");
        H2F hi_rec_pr = new H2F("hi_rec_pr","hi_rec_pr",100, 0., ebeam, 100, 0., 90.);
        hi_rec_pr.setTitleX("p (GeV)");
        hi_rec_pr.setTitleY("#theta (deg)");
        hi_rec_pr.setTitle("Proton");
        H1F hi_rec_rate = new H1F("hi_rec_rate","hi_rec_rate",250, 0.6, ebeam*0.6+0.6); 
        hi_rec_rate.setTitleX("W (GeV)");
        hi_rec_rate.setTitleY("Rate (Hz)");
        hi_rec_rate.setOptStat("1110");         
        DataGroup dg_reconstructed = new DataGroup(2,3);
        dg_reconstructed.addDataSet(hi_rec_q2w, 0);
        dg_reconstructed.addDataSet(hi_rec_w, 1);
        dg_reconstructed.addDataSet(hi_rec_el, 2);
        dg_reconstructed.addDataSet(hi_rec_pr, 3);        
        dg_reconstructed.addDataSet(hi_rec_rate, 4);
        this.getDataGroup().add(dg_reconstructed, 2);
        // Resolution
        H1F hi_res_el = new H1F("hi_res_el","hi_res_el",200, -2.5, 2.5);
        hi_res_el.setTitleX("#Delta(p)/p (%)");
        hi_res_el.setTitleY("Counts");
        hi_res_el.setTitle("Electron");
        hi_res_el.setOptStat("1110");    
        H1F hi_res_pr = new H1F("hi_res_pr","hi_res_pr",200, -15, 15);
        hi_res_pr.setTitleX("#Delta(p)/p (%)");
        hi_res_pr.setTitleY("Counts");
        hi_res_pr.setTitle("Proton");
        hi_res_pr.setOptStat("1110");    
        H1F hi_restheta_el = new H1F("hi_restheta_el","hi_restheta_el",200, -1, 1);
        hi_restheta_el.setTitleX("#Delta#theta (deg)");
        hi_restheta_el.setTitleY("Counts");
        hi_restheta_el.setTitle("Electron");
        hi_restheta_el.setOptStat("1110");    
        H1F hi_restheta_pr = new H1F("hi_restheta_pr","hi_restheta_pr",200, -3, 3);
        hi_restheta_pr.setTitleX("#Delta#theta (deg)");
        hi_restheta_pr.setTitleY("Counts");
        hi_restheta_pr.setTitle("Proton");
        hi_restheta_pr.setOptStat("1110");    
        DataGroup dg_resolution = new DataGroup(2,2);
        dg_resolution.addDataSet(hi_res_el, 0);
        dg_resolution.addDataSet(hi_res_pr, 1);
        dg_resolution.addDataSet(hi_restheta_el, 2);
        dg_resolution.addDataSet(hi_restheta_pr, 3);
        this.getDataGroup().add(dg_resolution, 3);
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
        H2F hi_Evsp_EC = new H2F("hi_Evsp_EC", "hi_Evsp_EC", 100, 0, ebeam, 100, 0, ebeam);  
        hi_Evsp_EC.setTitleX("p (GeV)"); 
        hi_Evsp_EC.setTitleY("E (GeV)");
        H2F hi_sfvsp_EC = new H2F("hi_sfvsp_EC", "hi_sfvsp_EC", 100, 0, ebeam, 100, 0, 0.6);  
        hi_sfvsp_EC.setTitleX("p (GeV)"); 
        hi_sfvsp_EC.setTitleY("E/p");       
        H2F hi_ECin_vs_PCAL = new H2F("hi_ECin_vs_PCAL", "hi_ECin_vs_PCAL", 100, 0, ebeam/4, 100, 0, ebeam/4);  
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
        H1F hi_dphi = new H1F("hi_dphi", "hi_dphi", 100, -360., 360.);   
        hi_dphi.setTitleX("dphi");
        hi_dphi.setTitleY("Counts");
        hi_dphi.setOptStat("1110");
        DataGroup dg_proton = new DataGroup(2,2);
        dg_proton.addDataSet(hi_E_pr, 0);
        dg_proton.addDataSet(hi_Evsp_pr, 1);
        dg_proton.addDataSet(hi_T_pr, 2);
        dg_proton.addDataSet(hi_betavsp_pr, 3);
        dg_proton.addDataSet(hi_dphi, 4);
        this.getDataGroup().add(dg_proton, 7);

       

    }
    
    @Override
    public void plotHistos() {
        this.getAnalysisCanvas().getCanvas("Generated").divide(2, 2);
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
        
        this.getAnalysisCanvas().getCanvas("Reconstructed").cd(0);
        this.getAnalysisCanvas().getCanvas("Reconstructed").draw(this.getDataGroup().getItem(2).getH1F("hi_rec_w"));
        this.getAnalysisCanvas().getCanvas("Reconstructed").cd(1);
        this.getAnalysisCanvas().getCanvas("Reconstructed").getPad(1).getAxisZ().setLog(true);
        this.getAnalysisCanvas().getCanvas("Reconstructed").draw(this.getDataGroup().getItem(2).getH2F("hi_rec_q2w"));
        this.getAnalysisCanvas().getCanvas("Reconstructed").cd(2);
        this.getAnalysisCanvas().getCanvas("Reconstructed").getPad(2).getAxisZ().setLog(true);
        this.getAnalysisCanvas().getCanvas("Reconstructed").draw(this.getDataGroup().getItem(2).getH2F("hi_rec_el"));
        this.getAnalysisCanvas().getCanvas("Reconstructed").cd(3);
        this.getAnalysisCanvas().getCanvas("Reconstructed").getPad(3).getAxisZ().setLog(true);
        this.getAnalysisCanvas().getCanvas("Reconstructed").draw(this.getDataGroup().getItem(2).getH2F("hi_rec_pr"));
        
        this.getAnalysisCanvas().getCanvas("Resolution").cd(0);
        this.getAnalysisCanvas().getCanvas("Resolution").draw(this.getDataGroup().getItem(3).getH1F("hi_res_el"));
        this.getAnalysisCanvas().getCanvas("Resolution").cd(1);
        this.getAnalysisCanvas().getCanvas("Resolution").draw(this.getDataGroup().getItem(3).getH1F("hi_res_pr"));
        this.getAnalysisCanvas().getCanvas("Resolution").cd(2);
        this.getAnalysisCanvas().getCanvas("Resolution").draw(this.getDataGroup().getItem(3).getH1F("hi_restheta_el"));
        this.getAnalysisCanvas().getCanvas("Resolution").cd(3);
        this.getAnalysisCanvas().getCanvas("Resolution").draw(this.getDataGroup().getItem(3).getH1F("hi_restheta_pr"));
        
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
        this.getAnalysisCanvas().getCanvas("Protons").draw(this.getDataGroup().getItem(7).getH1F("hi_dphi"));
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
        DataBank recCaloEB = null;
        DataBank recScinEB = null;
        DataBank recBankTB = null;
        DataBank recBankCVT = null;
        DataBank recBankFT = null;
        DataBank genBankMC = null;
        Particle genEl = null;
        Particle genPr = null;
        Particle recEl = null;
        Particle recNeg = null;
        Particle recPos = null;
        Particle recPr = null;
        Particle recFT = null;
        LorentzVector virtualPhoton = null;
        LorentzVector hadronSystem  = null;
        int ntracks = 0;
        int ncentral = 0;
        if(event.hasBank("REC::Particle")) recBankEB = event.getBank("REC::Particle");
        if(event.hasBank("REC::Calorimeter"))  recCaloEB = event.getBank("REC::Calorimeter");
        if(event.hasBank("REC::Scintillator")) recScinEB = event.getBank("REC::Scintillator");
        if(event.hasBank("TimeBasedTrkg::TBTracks")) recBankTB = event.getBank("TimeBasedTrkg::TBTracks");
        if(event.hasBank("CVTRec::Tracks")) recBankCVT = event.getBank("CVTRec::Tracks");
        if(event.hasBank("FT::particles")) recBankFT = event.getBank("FT::particles");
        if(event.hasBank("MC::Particle"))  genBankMC = event.getBank("MC::Particle");
        if(recBankTB!=null)  ntracks  = recBankTB.rows();
        if(recBankCVT!=null) ncentral = recBankCVT.rows();
        int nmc = 0;
        if(genBankMC!=null) {
            int nrows = genBankMC.rows();
            nmc = nrows;
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
                }
            }
            LorentzVector virtualPhotonGen = new LorentzVector(0., 0., ebeam, ebeam);
            virtualPhotonGen.sub(genEl.vector());
            LorentzVector hadronSystemGen = new LorentzVector(0., 0., ebeam, 0.9383+ebeam);
            hadronSystemGen.sub(genEl.vector());
            this.getDataGroup().getItem(1).getH2F("hi_gen_q2w").fill(hadronSystemGen.mass(),-virtualPhotonGen.mass2());
            this.getDataGroup().getItem(1).getH1F("hi_gen_w").fill(hadronSystemGen.mass());
//            this.getDataGroup().getItem(1).getH1F("hi_gen_rate").fill(hadronSystemGen.mass(),1/trun);
            nGen++;
        }
        if(recBankEB!=null && recCaloEB!=null /*&& ntracks==1 && ncentral==1 && nmc<=2*/) {
            int nrows = recBankEB.rows();
            for(int loop = 0; loop < nrows; loop++){
                int pidCode = 0;
                if(recBankEB.getInt("pid", loop)!=0) pidCode = recBankEB.getInt("pid", loop);
                else if(recBankEB.getByte("charge", loop)==-1) pidCode =  11;
                else if(recBankEB.getByte("charge", loop)==1)  pidCode =  2212;
                else pidCode = 22;
                Particle recParticle = new Particle(
                                            pidCode,
                                            recBankEB.getFloat("px", loop)*scale,
                                            recBankEB.getFloat("py", loop)*scale,
                                            recBankEB.getFloat("pz", loop)*scale,
                                            recBankEB.getFloat("vx", loop),
                                            recBankEB.getFloat("vy", loop),
                                            recBankEB.getFloat("vz", loop));
                int sector=0;
                double energy1=0;
                double energy4=0;
                double energy7=0;
                double time=0;
                double path=0;
                for(int j=0; j<recCaloEB.rows(); j++) {
                    if(recCaloEB.getShort("pindex",j)==loop && recCaloEB.getByte("detector",j)==16/*DetectorType.ECAL.getDetectorId()*/) {
                        if(energy1 >= 0 && recCaloEB.getByte("layer",j) == 1) energy1 += recCaloEB.getFloat("energy",j);
                        if(energy4 >= 0 && recCaloEB.getByte("layer",j) == 4) energy4 += recCaloEB.getFloat("energy",j);
                        if(energy7 >= 0 && recCaloEB.getByte("layer",j) == 7) energy7 += recCaloEB.getFloat("energy",j);
                        sector = recCaloEB.getInt("sector",j);
                    }
                } 
                for(int j=0; j<recCaloEB.rows(); j++) {
                    if(recScinEB.getShort("pindex",j)==loop && recScinEB.getByte("detector",j)==DetectorType.FTOF.getDetectorId() && recScinEB.getByte("layer",j) == 2) {
                        time = recCaloEB.getFloat("time",j);
                        path = recCaloEB.getFloat("path",j);
                    }
                }
                recParticle.setProperty("energy1",energy1);
                recParticle.setProperty("energy4",energy4);
                recParticle.setProperty("energy7",energy7);
                recParticle.setProperty("time",time);
                recParticle.setProperty("path",path);
                recParticle.setProperty("sector",sector);            
//                if(recBankTB!=null) recParticle.setProperty("sector",recBankTB.getByte("sector", loop)*1.0);
                if(recParticle.charge()==-1) {
                    if(recParticle.pid()==11 && recParticle.getProperty("sector")>0) recEl = recParticle;
                    recNeg = recParticle;
                    recNeg.changePid(11);
//                    if(this.momentumCheck(recParticle, genEl, 0.1))      recEl = recParticle;
                }
                else if(recParticle.charge()==1) {
                    if(recParticle.pid()==2212) recPr = recParticle;
                    recPos = recParticle;
                    recPos.changePid(2212);
//                    if(this.momentumCheck(recParticle, genPr, 0.1)) {
//                        recParticle.changePid(2212);
//                    }
                } 
                if(energy1>0.0 && energy4>0 && Math.abs(recParticle.vz()-2)<20. && recParticle.charge()==-1 && recEl!=null) {
                    double energy=(energy1+energy4+energy7)/0.245;
                    this.getDataGroup().getItem(6).getH2F("hi_ECin_vs_PCAL").fill(energy1,energy4+energy7);
                    if(energy1>0.1 && energy4>0.0 && recParticle.p()>0) {
                        this.getDataGroup().getItem(6).getH2F("hi_Evsp_EC").fill(recParticle.p(),energy);
                        this.getDataGroup().getItem(6).getH2F("hi_sfvsp_EC").fill(recParticle.p(),(energy1+energy4+energy7)/recParticle.p());
                        this.getDataGroup().getItem(6).getH1F("hi_dE_EC").fill(energy-recParticle.p());
                        this.getDataGroup().getItem(6).getH1F("hi_sf_EC").fill((energy1+energy4+energy7)/recParticle.p());
                    }
                }
            }
            if(recPr != null) {
                double energy = recPr.getProperty("energy1") + recPr.getProperty("energy4") + recPr.getProperty("energy7");
                this.getDataGroup().getItem(7).getH1F("hi_E_pr").fill(energy);
                this.getDataGroup().getItem(7).getH2F("hi_Evsp_pr").fill(recPr.p(),energy);
                double beta = 0;
                double time = recPr.getProperty("time");
                if(time>124.25) beta=recPr.getProperty("path")/(time-124.5)/29.97;
                this.getDataGroup().getItem(7).getH1F("hi_T_pr").fill(time-124.25);
                this.getDataGroup().getItem(7).getH2F("hi_betavsp_pr").fill(recPr.p(),beta);
            }
        }
//        if(recBankFT != null) {
//            int nrows = recBankFT.rows();
//            for(int loop = 0; loop < nrows; loop++){
//                if(recBankFT.getByte("charge",loop)==-1) {
//                    Particle recParticle = new Particle(
//                                            11,
//                                            recBankFT.getFloat("cx", loop)*recBankFT.getFloat("energy", loop),
//                                            recBankFT.getFloat("cy", loop)*recBankFT.getFloat("energy", loop),
//                                            recBankFT.getFloat("cz", loop)*recBankFT.getFloat("energy", loop),
//                                            0,0,0);
//                    if(recEl == null || true) {
//                        if(this.momentumCheck(recParticle, genEl,0.2)) {
//                            recFT = recParticle;
//                            recEl = recParticle;
//                            nElec++;
//                        }
//                    }
//                }
//            }
//        }
//                genBankMC.show();
//                recBankEB.show();
        if(genEl != null) this.getDataGroup().getItem(1).getH2F("hi_gen_el").fill(genEl.p(),Math.toDegrees(genEl.theta()));
        if(genPr != null) this.getDataGroup().getItem(1).getH2F("hi_gen_pr").fill(genPr.p(),Math.toDegrees(genPr.theta()));
        if(recEl != null) this.getDataGroup().getItem(2).getH2F("hi_rec_el").fill(recEl.p(),Math.toDegrees(recEl.theta()));
        if(recPr != null) this.getDataGroup().getItem(2).getH2F("hi_rec_pr").fill(recPr.p(),Math.toDegrees(recPr.theta()));
        if(recEl != null) {
            System.out.println("Analyzed ");
            virtualPhoton = new LorentzVector(0., 0., ebeam, ebeam);
            virtualPhoton.sub(recEl.vector());
            hadronSystem = new LorentzVector(0., 0., ebeam, 0.9383+ebeam);
            hadronSystem.sub(recEl.vector());
            if(Math.toDegrees(recEl.theta())>0){
                this.getDataGroup().getItem(2).getH2F("hi_rec_q2w").fill(hadronSystem.mass(),-virtualPhoton.mass2());
                this.getDataGroup().getItem(2).getH1F("hi_rec_w").fill(hadronSystem.mass());
            }
            if(recPr != null && hadronSystem.mass()<1.1) this.getDataGroup().getItem(7).getH1F("hi_dphi").fill(Math.toDegrees(recPr.phi()-recEl.phi()));//            this.getDataGroup().getItem(2).getH1F("hi_rec_rate").fill(hadronSystem.mass(),1/trun);
            if(genEl != null && hadronSystem.mass()<1.1) {
                nElec++;
                this.getDataGroup().getItem(3).getH1F("hi_res_el").fill((recEl.p()-genEl.p())*100/genEl.p());
                this.getDataGroup().getItem(3).getH1F("hi_restheta_el").fill(Math.toDegrees(recEl.theta()-genEl.theta()));
                if(genPr != null && recPr != null) {
                    nPr++;
                    this.getDataGroup().getItem(3).getH1F("hi_res_pr").fill((recPr.p()-genPr.p())*100/genPr.p());
                    this.getDataGroup().getItem(3).getH1F("hi_restheta_pr").fill(Math.toDegrees(recPr.theta()-genPr.theta()));
                }
            }
        }
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
        H1F hcounts = this.getDataGroup().getItem(1).getH1F("hi_gen_w");
        H1F hrate   = this.getDataGroup().getItem(1).getH1F("hi_gen_rate");
        for(int i=0; i<hcounts.getData().length; i++) {
            hrate.setBinContent(i, hcounts.getBinContent(i)/(trun*nEvent));
        }
        hcounts = this.getDataGroup().getItem(2).getH1F("hi_rec_w");
        hrate   = this.getDataGroup().getItem(2).getH1F("hi_rec_rate");
        for(int i=0; i<hcounts.getData().length; i++) {
            hrate.setBinContent(i, hcounts.getBinContent(i)/(trun*nEvent));
        }
        System.out.println("Analyzed " + nEvent + " events and found " + nGen + " MC events, " + nElec + " electrons in the elastic peak, " 
                + nPr + " protons " + 1/(trun) + " rate generated (Hz)" + nPr/(trun*nEvent) + " rate reconstructed (Hz)");
    }

    @Override
    public void timerUpdate() {
        this.analyze();
        // fitting sampling fraction
//        this.fitGauss(this.getDataGroup().getItem(6).getH1F("hi_sf_EC"),this.getDataGroup().getItem(6).getF1D("f1_sf"));
//        this.getAcceptance(this.getDataGroup().getItem(1).getH1F("hi_gen_eg"), this.getDataGroup().getItem(4).getH1F("hi_im_eg"), this.getDataGroup().getItem(4).getH1F("hi_im_eg_acc"));
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
