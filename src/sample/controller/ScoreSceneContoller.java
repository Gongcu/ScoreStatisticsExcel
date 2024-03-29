package sample.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.poi.ss.formula.functions.Index;
import sample.ExcelController.ScoreExcelWriter;
import sample.data.PrimaryData;
import sample.data.ScoreData;
import sample.data.ScoreDataSub;
import sample.dialog.Dialog;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ScoreSceneContoller implements Initializable {

    @FXML
    private ComboBox comboBox;
    @FXML
    private Button exitButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button mainButton;

    @FXML
    private TableView<ScoreData> tableView;
    @FXML
    private TableColumn<ScoreData, Integer> tableClass;
    @FXML
    private TableColumn<ScoreData, String> tableName;
    @FXML
    private TableColumn<ScoreData, Integer> tableId;
    @FXML
    private TableColumn<ScoreData, Float> tableKor;
    @FXML
    private TableColumn<ScoreData, Float> tableEng;
    @FXML
    private TableColumn<ScoreData, Float> tableMath;
    @FXML
    private TableColumn<ScoreData, Float> tableSoc;
    @FXML
    private TableColumn<ScoreData, Float> tableSci;
    @FXML
    private TableColumn<ScoreData, Float> tableMus;
    @FXML
    private TableColumn<ScoreData, Float> tableArt;
    @FXML
    private TableColumn<ScoreData, Float> tableSpo;
    @FXML
    private TableColumn<ScoreData, Float> tableAvg;
    @FXML
    private TableColumn<ScoreData, Integer> tableRank;
    @FXML
    private TextField upPercentText;
    @FXML
    private TextField downPercentText;
    @FXML
    private Button getUpButton;
    @FXML
    private Button getDownButton;
    @FXML
    private Label student_number;

    private static final NumberFormatException ERROR_FOR_ALERTDIALOG = new NumberFormatException();

    private List<ScoreData> data = Controller.getData();
    private ObservableList<ScoreData> newList = FXCollections.observableArrayList();
    private ObservableList<ScoreData> primaryTableList = FXCollections.observableArrayList(); //for % getUp,DownValue Method
    private int percent;
    private int percent_mode; //up인지 down인지 구분해서 rank 얻기 위한 변수
    private Insert_mode insert_mode; //어느 과목 추출한지 확인 위한 enum 변수
    private ScoreDataSub[] subData; //table에서 rank와 avg 세팅

    enum Insert_mode{ALL, MAIN, KEM, MS, E}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dataInit();
        ScoreExcelWriter excelWriter = new ScoreExcelWriter();
        //Combo box 아이템 따라 성적 산출
        comboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                tableView.getColumns().clear();
                //tableView.getColumns().remove(); 선택적 컬럼 제거 가능할듯 반번호이름은 공통되니 냅두고 나머지만 삭제하는 방식으로 만들어보기
                if (t1.equals("전과목")) {
                    insert_mode=Insert_mode.ALL;
                    Insert_all();
                } else if (t1.equals("국영수사과")) {
                    insert_mode=Insert_mode.MAIN;
                    Insert_main();
                } else if (t1.equals("국영수")) {
                    insert_mode=Insert_mode.KEM;
                    Insert_kem();
                } else if (t1.equals("수학과학")) {
                    insert_mode=Insert_mode.MS;
                    Insert_ms();
                } else {
                    insert_mode=Insert_mode.E;
                    Insert_eng();
                }
            }
        });

        saveButton.setOnMouseClicked(event -> {
            //tableView item이 없을 때 경고창으로 예외처리
            try {
                if(PrimaryData.getExt().equals("xlsx"))
                    excelWriter.xlsxWiter(getTableItem());
                else if(PrimaryData.getExt().equals("xls"))
                    excelWriter.xlsWriter(getTableItem());
            }catch (IndexOutOfBoundsException e){Dialog.getDialog(e);}
        });


        /*메인화면 getScene*/
        mainButton.setOnMouseClicked(event ->{
            try {
                Parent root = FXMLLoader.load(getClass().getResource("../fxml/main.fxml"));
                PrimaryData.setSelectedFileName("파일이 이미 로딩 되어 있습니다.");
                Scene scene = new Scene(root);
                Stage stage = (Stage) exitButton.getScene().getWindow();
                stage.setScene(scene);
            }catch (IOException e){
                e.printStackTrace();
            }
        }); //main.fxml로 이동하는 메서드
        /*종료*/
        exitButton.setOnMouseClicked(event ->{
            Platform.exit();
        });
        /*percent값 받기*/

        getUpButton.setOnMouseClicked(event ->{
            getPercentValue(0,percent, primaryTableList);
        });
        getDownButton.setOnMouseClicked(event ->{
            getPercentValue(1,percent, primaryTableList);
        });
    }

    private void Insert_all() {
        //initialize new table column
        tableClass = new TableColumn("반");
        tableId = new TableColumn("번호");
        tableName = new TableColumn("이름");
        tableKor = new TableColumn("국어");
        tableEng = new TableColumn("영어");
        tableMath = new TableColumn("수학");
        tableSoc = new TableColumn("사회");
        tableSci = new TableColumn("과학");
        tableMus = new TableColumn("음악");
        tableArt = new TableColumn("미술");
        tableSpo = new TableColumn("체육");
        tableAvg = new TableColumn("평균");
        tableRank = new TableColumn("석차");

        initColumnDataByItem();

        tableView.getColumns().addAll(tableClass, tableId, tableName, tableKor, tableEng, tableMath, tableSoc, tableSci, tableMus, tableArt, tableSpo, tableAvg, tableRank);

        tableViewSetter();
    }

    private void Insert_main() {
        //initialize new table column
        tableClass = new TableColumn("반");
        tableId = new TableColumn("번호");
        tableName = new TableColumn("이름");
        tableKor = new TableColumn("국어");
        tableEng = new TableColumn("영어");
        tableMath = new TableColumn("수학");
        tableSoc = new TableColumn("사회");
        tableSci = new TableColumn("과학");
        tableMus = null;
        tableArt = null;
        tableSpo = null;
        tableAvg = new TableColumn("평균");
        tableRank = new TableColumn("석차");

        initColumnDataByItem();

        tableView.getColumns().addAll(tableClass, tableId, tableName, tableKor, tableEng, tableMath, tableSoc, tableSci,tableAvg,tableRank);

        tableViewSetter();
    }

    private void Insert_kem() {
        //initialize new table column
        tableClass = new TableColumn("반");
        tableId = new TableColumn("번호");
        tableName = new TableColumn("이름");
        tableKor = new TableColumn("국어");
        tableEng = new TableColumn("영어");
        tableMath = new TableColumn("수학");
        tableSoc = null;
        tableSci = null;
        tableMus = null;
        tableArt = null;
        tableSpo = null;
        tableAvg = new TableColumn("평균");
        tableRank = new TableColumn("석차");

        initColumnDataByItem();

        //tableView.getItems().clear();
        tableView.getColumns().addAll(tableClass, tableId, tableName, tableKor, tableEng, tableMath,tableAvg,tableRank);

        tableViewSetter();
    }

    private void Insert_ms() {
        //initialize new table column
        tableClass = new TableColumn("반");
        tableId = new TableColumn("번호");
        tableName = new TableColumn("이름");
        tableMath = new TableColumn("수학");
        tableSci = new TableColumn("과학");
        tableKor = null;
        tableEng = null;
        tableSoc = null;
        tableMus = null;
        tableArt = null;
        tableSpo = null;
        tableAvg = new TableColumn("평균");
        tableRank = new TableColumn("석차");

        initColumnDataByItem();

        tableAvg.setCellValueFactory(cellData -> cellData.getValue().avgProperty().asObject());
        tableRank.setCellValueFactory(cellData -> cellData.getValue().rankProperty().asObject());

        //tableView.getItems().clear();
        tableView.getColumns().addAll(tableClass, tableId, tableName, tableMath, tableSci,tableAvg,tableRank);

        tableViewSetter();
    }

    private void Insert_eng() {
        //initialize new table column
        tableClass = new TableColumn("반");
        tableId = new TableColumn("번호");
        tableName = new TableColumn("이름");
        tableEng = new TableColumn("영어");
        tableKor = null;
        tableMath = null;
        tableSci = null;
        tableSoc = null;
        tableMus = null;
        tableArt = null;
        tableSpo = null;
        tableAvg = new TableColumn("평균");
        tableRank = new TableColumn("석차");

        initColumnDataByItem();

        tableView.getColumns().addAll(tableClass, tableId, tableName, tableEng,tableAvg,tableRank);

        tableViewSetter();
    }


    private void dataInit() {
        student_number.setText(data.size()+""); //전교생 수 설정
        for (int i = 0; i < data.size(); i++) {
            //myList.add(data.get(i));
            newList.add(new ScoreData(data.get(i).getClass_num(), data.get(i).getId(), data.get(i).getName(), data.get(i).getKor(),
                    data.get(i).getEng(), data.get(i).getMath(), data.get(i).getSoc(), data.get(i).getSci(), data.get(i).getMus(), data.get(i).getArt(),
                    data.get(i).getSpo()));
        }
    }

    //TABLE VALUE GETTER, ScoreExcelWriter.xlsxWriter에 넘겨줄 list, 열이 있는 컬럼에서만 값을 받고 컬럼 유무가 저장되어 있음 *line:118
    private ObservableList<ScoreData> getTableItem() {
        ScoreData getData = new ScoreData();
        ObservableList<ScoreData> obList = FXCollections.observableArrayList();

        for (int i = 0; i < tableView.getItems().size(); i++) {
            getData = tableView.getItems().get(i);
            obList.add(new ScoreData());
            obList.get(i).setClass_num(getData.getClass_num());
            obList.get(i).setId(getData.getId());
            obList.get(i).setName(getData.getName());
            if (tableKor != null) {
                obList.get(i).setKor(getData.getKor());
                obList.get(i).setKorHeader(true);
            }
            else
                obList.get(i).setKorHeader(false);

            if (tableEng != null) {
                obList.get(i).setEng(getData.getEng());
                obList.get(i).setEngHeader(true);
            }
            else
                obList.get(i).setEngHeader(false);

            if (tableMath != null) {
                obList.get(i).setMath(getData.getMath());
                obList.get(i).setMathHeader(true);
            }
            else
                obList.get(i).setMathHeader(false);

            if (tableSoc != null) {
                obList.get(i).setSoc(getData.getSoc());
                obList.get(i).setSocHeader(true);
            }
            else
                obList.get(i).setSocHeader(false);

            if (tableSci != null) {
                obList.get(i).setSci(getData.getSci());
                obList.get(i).setSciHeader(true);
            }
            else
                obList.get(i).setSciHeader(false);

            if (tableMus != null) {
                obList.get(i).setMus(getData.getMus());
                obList.get(i).setMusHeader(true);
            }
            else
                obList.get(i).setMusHeader(false);

            if (tableArt != null) {
                obList.get(i).setArt(getData.getArt());
                obList.get(i).setArtHeader(true);
            }
            else
                obList.get(i).setArtHeader(false);

            if (tableSpo != null) {
                obList.get(i).setSpo(getData.getSpo());
                obList.get(i).setSpoHeader(true);
            }
            else
                obList.get(i).setSpoHeader(false);

            obList.get(i).setAvg(getData.getAvg());
            obList.get(i).setRank(getData.getRank());
        }

        return obList;
    }

    private void getSubData(){
        //----------for return------
        subData = new ScoreDataSub[tableView.getItems().size()];
        //----------for avg---------
        float sum=0;
        int subject_num=0;
        ScoreData tableData = new ScoreData();
        for (int i = 0; i < tableView.getItems().size(); i++) {
            tableData = tableView.getItems().get(i);
            if(tableKor!=null) {
                sum += tableData.getKor();
                subject_num++;
            }
            if(tableEng!=null) {
                sum += tableData.getEng();
                subject_num++;
            }
            if(tableMath!=null) {
                sum += tableData.getMath();
                subject_num++;
            }
            if(tableSoc!=null) {
                sum += tableData.getSoc();
                subject_num++;
            }
            if(tableSci!=null) {
                sum += tableData.getSci();
                subject_num++;
            }
            if(tableMus!=null) {
                sum += tableData.getMus();
                subject_num++;
            }
            if(tableArt!=null) {
                sum += tableData.getArt();
                subject_num++;
            }
            if(tableSpo!=null) {
                sum += tableData.getSpo();
                subject_num++;
            }
            subData[i] = new ScoreDataSub();
            subData[i].setAvg(sum/subject_num);
            sum=0;
            subject_num=0;
        }
        //----------for rank--------
        ScoreData prevData = new ScoreData();
        ScoreData nextData = new ScoreData();
        float prevSum=0;
        float nextSum=0;
        if(percent_mode==0) {
            for (int i = 0; i < tableView.getItems().size(); i++) {
                subData[i].setRank(1); //제일 높은 순위인 1등 부터 시작해서 다음 값들과 비교 뒤 작은 만큼 계속 순위 증가하는 알고리즘
                for (int j = 0; j < tableView.getItems().size(); j++) {
                    prevData = tableView.getItems().get(i);
                    nextData = tableView.getItems().get(j);
                    if (tableKor != null) {
                        prevSum += prevData.getKor();
                        nextSum += nextData.getKor();
                        subject_num++;
                    }
                    if (tableEng != null) {
                        prevSum += prevData.getEng();
                        nextSum += nextData.getEng();

                    }
                    if (tableMath != null) {
                        prevSum += prevData.getMath();
                        nextSum += nextData.getMath();
                    }
                    if (tableSoc != null) {
                        prevSum += prevData.getSoc();
                        nextSum += nextData.getSoc();
                    }
                    if (tableSci != null) {
                        prevSum += prevData.getSci();
                        nextSum += nextData.getSci();
                    }
                    if (tableMus != null) {
                        prevSum += prevData.getMus();
                        nextSum += nextData.getMus();
                    }
                    if (tableArt != null) {
                        prevSum += prevData.getArt();
                        nextSum += nextData.getArt();
                    }
                    if (tableSpo != null) {
                        prevSum += prevData.getSpo();
                        nextSum += nextData.getSpo();
                    }
                    if (prevSum < nextSum)
                        subData[i].setRank(subData[i].getRank()+1);
                    nextSum = 0;
                    prevSum = 0;
                }
            }
        }
        else if(percent_mode==1) {
            for (int i = 0; i < tableView.getItems().size(); i++) {
                subData[i].setRank(Integer.parseInt(student_number.getText()));//제일 높은 순위인 학생 수(99)부터 시작해서 현재 값이 클수록 감소하는 알고리즘
                for (int j = 0; j < tableView.getItems().size(); j++) {
                    prevData = tableView.getItems().get(i);
                    nextData = tableView.getItems().get(j);
                    if (tableKor != null) {
                        prevSum += prevData.getKor();
                        nextSum += nextData.getKor();
                    }
                    if (tableEng != null) {
                        prevSum += prevData.getEng();
                        nextSum += nextData.getEng();

                    }
                    if (tableMath != null) {
                        prevSum += prevData.getMath();
                        nextSum += nextData.getMath();
                    }
                    if (tableSoc != null) {
                        prevSum += prevData.getSoc();
                        nextSum += nextData.getSoc();
                    }
                    if (tableSci != null) {
                        prevSum += prevData.getSci();
                        nextSum += nextData.getSci();
                    }
                    if (tableMus != null) {
                        prevSum += prevData.getMus();
                        nextSum += nextData.getMus();
                    }
                    if (tableArt != null) {
                        prevSum += prevData.getArt();
                        nextSum += nextData.getArt();
                    }
                    if (tableSpo != null) {
                        prevSum += prevData.getSpo();
                        nextSum += nextData.getSpo();
                    }
                    if (prevSum > nextSum)
                        subData[i].setRank(subData[i].getRank()-1);
                    nextSum = 0;
                    prevSum = 0;
                }
            }
        }
    }

    private void getPercentValue(int num, int percent, List<ScoreData> list){
        //num으로 상위 하위 % 구분, try문 안에 코드 다 넣어야된다. 밑에 빼면 percent 값이 없는데도 진행
        try {
            if(num==0)
                percent = Integer.parseInt(upPercentText.getText());
            else
                percent = Integer.parseInt(downPercentText.getText());
            if(percent>100 || percent<0)
                Dialog.getDialog(ERROR_FOR_ALERTDIALOG);
            ObservableList<ScoreData> resultList = FXCollections.observableArrayList();
            int student_num =(list.size()*percent)/100;
            Collections.sort(list);
            if(num==0) {
                for (int i = 0; i < student_num; i++)
                    resultList.add(list.get(i));
            }
            else {
                for(int i=list.size(); i>list.size()-student_num; i--)
                    resultList.add(list.get(i-1));
            }
            tableView.setItems(resultList);
            upPercentText.setText("");
            downPercentText.setText("");
        }catch (NumberFormatException e) {Dialog.getDialog(e);}
    }

    private void initColumnDataByItem(){
        tableName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        tableClass.setCellValueFactory(cellData -> cellData.getValue().class_numProperty().asObject());
        tableId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        if(tableKor!=null)
            tableKor.setCellValueFactory(cellData -> cellData.getValue().korProperty().asObject());
        if(tableEng!=null)
            tableEng.setCellValueFactory(cellData -> cellData.getValue().engProperty().asObject());
        if(tableMath!=null)
            tableMath.setCellValueFactory(cellData -> cellData.getValue().mathProperty().asObject());
        if(tableSoc!=null)
            tableSoc.setCellValueFactory(cellData -> cellData.getValue().socProperty().asObject());
        if(tableSci!=null)
            tableSci.setCellValueFactory(cellData -> cellData.getValue().sciProperty().asObject());
        if(tableMus!=null)
            tableMus.setCellValueFactory(cellData -> cellData.getValue().musProperty().asObject());
        if(tableArt!=null)
            tableArt.setCellValueFactory(cellData -> cellData.getValue().artProperty().asObject());
        if(tableSpo!=null)
            tableSpo.setCellValueFactory(cellData -> cellData.getValue().spoProperty().asObject());
        tableAvg.setCellValueFactory(cellData -> cellData.getValue().avgProperty().asObject());
        tableRank.setCellValueFactory(cellData -> cellData.getValue().rankProperty().asObject());
    }

    private void tableViewSetter(){
        tableView.setItems(newList); // 밑에 getRank,getAvg 메서드를 진행하려면 setting된 테이블이 필요함
        //int[] rank = getRank();
        //float[] avg = getAvg();
        getSubData();
        for(int i=0; i<newList.size(); i++){
            newList.get(i).setRank(subData[i].getRank());
            newList.get(i).setAvg(subData[i].getAvg());
        }
        primaryTableList=newList;
        tableView.setItems(newList); // finally add data to tableview
    }
}

