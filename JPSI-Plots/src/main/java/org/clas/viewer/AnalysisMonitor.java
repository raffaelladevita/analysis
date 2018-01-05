/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.clas.viewer;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import org.jlab.detector.view.DetectorPane2D;
import org.jlab.groot.base.GStyle;
import org.jlab.groot.data.IDataSet;
import org.jlab.groot.data.TDirectory;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import org.jlab.groot.group.DataGroup;
import org.jlab.io.base.DataEvent;
import org.jlab.io.base.DataEventType;
import org.jlab.io.task.IDataEventListener;
import org.jlab.utils.groups.IndexedList;

/**
 *
 * @author devita
 */
public class AnalysisMonitor implements IDataEventListener {    
    
    private final String           analysisName;
    private ArrayList<String>      analysisTabNames  = new ArrayList();
    private IndexedList<DataGroup> analysisData    = new IndexedList<DataGroup>(1);
    private DataGroup              analysisSummary = null;
    private JPanel                 analysisPanel   = null;
    private EmbeddedCanvasTabbed   analysisCanvas  = null;
    private DetectorPane2D         analysisView    = null;
    private int                    numberOfEvents;

    public AnalysisMonitor(String name){
        GStyle.getAxisAttributesX().setTitleFontSize(24);
        GStyle.getAxisAttributesX().setLabelFontSize(18);
        GStyle.getAxisAttributesY().setTitleFontSize(24);
        GStyle.getAxisAttributesY().setLabelFontSize(18);
        this.analysisName = name;
        this.analysisPanel  = new JPanel();
        this.analysisCanvas = new EmbeddedCanvasTabbed();
        this.analysisView   = new DetectorPane2D();
        this.numberOfEvents = 0;
    }

    
    public void analyze() {
        // analyze detector data at the end of data processing
    }

    public void createHistos() {
        // initialize canvas and create histograms
    }
    
    @Override
    public void dataEventAction(DataEvent event) {
        
        this.setNumberOfEvents(this.getNumberOfEvents()+1);
        if (event.getType() == DataEventType.EVENT_START) {
//            resetEventListener();
            processEvent(event);
	} else if (event.getType() == DataEventType.EVENT_SINGLE) {
            processEvent(event);
            plotEvent(event);
	} else if (event.getType() == DataEventType.EVENT_ACCUMULATE) {
            processEvent(event);
	} else if (event.getType() == DataEventType.EVENT_STOP) {
            System.out.println("Event stop ...");
            analyze();
	}
    }

    public void drawDetector() {
    
    }
    
    public EmbeddedCanvasTabbed getAnalysisCanvas() {
        return analysisCanvas;
    }
    
    public ArrayList<String> getAnalysisTabNames() {
        return analysisTabNames;
    }
    
    public IndexedList<DataGroup>  getDataGroup(){
        return analysisData;
    }

    public String getAnalysisName() {
        return analysisName;
    }
    
    public JPanel getAnalysisPanel() {
        return analysisPanel;
    }
    
    public DataGroup getAnalysisSummary() {
        return analysisSummary;
    }
    
    public DetectorPane2D getAnalysisView() {
        return analysisView;
    }
    
    public int getNumberOfEvents() {
        return numberOfEvents;
    }

    public void init(boolean flagDetectorView) {
        // initialize monitoring application
        // detector view is shown if flag is true
        getAnalysisPanel().setLayout(new BorderLayout());
        drawDetector();
        JSplitPane   splitPane = new JSplitPane();
        splitPane.setLeftComponent(getAnalysisView());
        splitPane.setRightComponent(getAnalysisCanvas());
        if(flagDetectorView) {
            getAnalysisPanel().add(splitPane,BorderLayout.CENTER);  
        }
        else {
            getAnalysisPanel().add(getAnalysisCanvas(),BorderLayout.CENTER);  
        }
        createHistos();
        plotHistos();
    }
    
    public void processEvent(DataEvent event) {
        // process event
    }
    
    public void plotEvent(DataEvent event) {
        // process event
    }
    
    public void plotHistos() {

    }
    
    public void printCanvas(String dir) {
        // print canvas to files
        for(int tab=0; tab<this.analysisTabNames.size(); tab++) {
            String fileName = dir + "/" + this.analysisName + "_" + this.analysisTabNames.get(tab) + ".png";
            System.out.println(fileName);
            this.analysisCanvas.getCanvas(this.analysisTabNames.get(tab)).save(fileName);
        }
    }
    
    @Override
    public void resetEventListener() {
        System.out.println("Resetting " + this.getAnalysisName() + " histogram");
        this.createHistos();
        this.plotHistos();
    }
    
    public void setAnalysisCanvas(EmbeddedCanvasTabbed canvas) {
        this.analysisCanvas = canvas;
    }
    
    public void setAnalysisSummary(DataGroup group) {
        this.analysisSummary = group;
    }
    
    public void setAnalysisTabNames(String... names) {
        for(String name : names) {
            this.analysisTabNames.add(name);
        }
        EmbeddedCanvasTabbed canvas = new EmbeddedCanvasTabbed(names);
        this.setAnalysisCanvas(canvas);
    }

    public void setCanvasUpdate(int time) {
        for(int tab=0; tab<this.analysisTabNames.size(); tab++) {
            this.analysisCanvas.getCanvas(this.analysisTabNames.get(tab)).initTimer(time);
        }
    }
    
    public void setNumberOfEvents(int numberOfEvents) {
        this.numberOfEvents = numberOfEvents;
    }

    @Override
    public void timerUpdate() {
        
    }

    public void readDataGroup(TDirectory dir) {
        String folder = this.getAnalysisName() + "/";
        System.out.println("Reading from: " + folder);
        DataGroup sum = this.getAnalysisSummary();
        int nrows = sum.getRows();
        int ncols = sum.getColumns();
        int nds   = nrows*ncols;
        DataGroup newSum = new DataGroup(ncols,nrows);
        for(int i = 0; i < nds; i++){
            List<IDataSet> dsList = sum.getData(i);
            for(IDataSet ds : dsList){
                System.out.println("\t --> " + ds.getName());
                newSum.addDataSet(dir.getObject(folder, ds.getName()),i);
            }
        }            
        this.setAnalysisSummary(newSum);
        Map<Long, DataGroup> map = this.getDataGroup().getMap();
        for( Map.Entry<Long, DataGroup> entry : map.entrySet()) {
            Long key = entry.getKey();
            DataGroup group = entry.getValue();
            nrows = group.getRows();
            ncols = group.getColumns();
            nds   = nrows*ncols;
            DataGroup newGroup = new DataGroup(ncols,nrows);
            for(int i = 0; i < nds; i++){
                List<IDataSet> dsList = group.getData(i);
                for(IDataSet ds : dsList){
                    System.out.println("\t --> " + ds.getName());
                    newGroup.addDataSet(dir.getObject(folder, ds.getName()),i);
                }
            }
            map.replace(key, newGroup);
        }
        this.plotHistos();
    }
    
    public void writeDataGroup(TDirectory dir) {
        String folder = "/" + this.getAnalysisName();
        dir.mkdir(folder);
        dir.cd(folder);
        DataGroup sum = this.getAnalysisSummary();
        int nrows = sum.getRows();
        int ncols = sum.getColumns();
        int nds   = nrows*ncols;
        for(int i = 0; i < nds; i++){
            List<IDataSet> dsList = sum.getData(i);
            for(IDataSet ds : dsList){
                System.out.println("\t --> " + ds.getName());
                dir.addDataSet(ds);
            }
        }            
        Map<Long, DataGroup> map = this.getDataGroup().getMap();
        for( Map.Entry<Long, DataGroup> entry : map.entrySet()) {
            DataGroup group = entry.getValue();
            nrows = group.getRows();
            ncols = group.getColumns();
            nds   = nrows*ncols;
            for(int i = 0; i < nds; i++){
                List<IDataSet> dsList = group.getData(i);
                for(IDataSet ds : dsList){
                    System.out.println("\t --> " + ds.getName());
                    dir.addDataSet(ds);
                }
            }
        }
    }

    
}
