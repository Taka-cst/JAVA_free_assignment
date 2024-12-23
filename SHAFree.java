//JAVA自由課題

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;

//ラジオボタン用
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

//ドロップダウン用
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

//コピペ用
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

//ファイル入出力用
import java.nio.file.Files;
import java.nio.file.Paths;

//Base64エンコード用
import java.util.Base64;
import java.math.BigInteger;

//メッセージダイジェスト用
import java.security.MessageDigest;



public class SHAFree extends JFrame implements ActionListener{
    JLabel label1,label2,label3;//それぞれのラベル
    ButtonGroup group; //ラジオボタンの重複を防ぐためのグループ
    JTextField field,checksum;//field=>文字列やファイルパス, checksum=>チェックサムがあれば入力
    JRadioButton mozi,file;//mozi=>文字から生成,file=>ファイルから生成
    JButton generate,copy;//生成ボタンとコピーボタン
    JTextArea result;//生成結果や比較結果の表示
    JComboBox comboBox;//ドロップダウンリスト

    byte [] nakami;//ファイルの中身を格納するための変数


    String[] FromText = {"MD5", "ROT13", "SHA-256","To Base64", "From Base64"};
    String[] FromFile = {"MD5", "SHA-256", "SHA-512"};
    public static void main(String[] args){
        SHAFree w = new SHAFree();
        w.setTitle("SHAジェネレータ");
        w.setSize(520, 300);
        w.setVisible(true);
    }

    public SHAFree(){
        setLayout(new BorderLayout()); // BorderLayoutを設定
        mozi=new JRadioButton("文字から生成",true);
        file=new JRadioButton("ファイルから生成");
        group=new ButtonGroup();
        group.add(mozi);
        group.add(file);
        label1=new JLabel("生成の種類:");
        label2=new JLabel("元のテキスト/ファイルパス:");
        label3=new JLabel("チェックサム(任意)");
        field=new JTextField(30);
        checksum= new JTextField(30);
        result= new JTextArea(10, 40);
        result.setEditable(false);
        result.setLineWrap(true);
        result.setWrapStyleWord(true);
        generate= new JButton("生成/比較");
        copy= new JButton(String.format("結果\nコピー"));
        //配置
        JPanel panel1=new JPanel(new GridLayout(3, 2));//ボタン以外の部分
        JPanel panel1_1=new JPanel(new GridLayout(1, 2));
        panel1_1.add(mozi);
        panel1_1.add(file);
        panel1.add(panel1_1);

        comboBox= new JComboBox(FromText);
        panel1.add(comboBox);
        panel1.add(label2);
        panel1.add(field);
        panel1.add(label3);
        panel1.add(checksum);
        add(panel1, BorderLayout.NORTH);
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout());
        JLabel resultLabel = new JLabel("<html><br>生成結果<html>");
        resultLabel.setHorizontalAlignment(JLabel.CENTER);//ラベルの中央ぞろえ
        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultPanel.add(result, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.CENTER);

        // add(result, BorderLayout.CENTER);
        JPanel panel2 = new JPanel();//生成ボタンとコピーボタン
        panel2.add(generate);
        panel2.add(copy);
        add(panel2, BorderLayout.SOUTH);

        //アクションパフォームド追加
        mozi.addActionListener(this);
        file.addActionListener(this);
        generate.addActionListener(this);
        copy.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae){//
        if(ae.getSource()==mozi){//ラジオボタンによってドロップダウンリストの中身を変更
            comboBox.setModel(new DefaultComboBoxModel(FromText));//参考[https://docs.oracle.com/javase/jp/8/docs/api/javax/swing/DefaultComboBoxModel.html]
        }else if(ae.getSource()==file){//同じ
            comboBox.setModel(new DefaultComboBoxModel(FromFile));
        }else if(ae.getSource()==generate){//ボタンが押されたら
            gen();
        }else if(ae.getSource()==copy){
            cop();
        }
    }

    //生成ボタンが押されたときの処理
    public void gen(){
        String text=field.getText();
        if(text.equals("")){
            JOptionPane.showMessageDialog(null, "テキストを入力してください");
        }else if(!(mozi.isSelected()||file.isSelected())){
            JOptionPane.showMessageDialog(null, "生成の種類を選択してください");
        }
        //変換タイプの取得
        String type=(String)comboBox.getSelectedItem();
        //System.out.println(comboBox.getSelectedItem());
        if(mozi.isSelected()){//文字から生成する場合
        	//MessageDigest関連はhttps://docs.oracle.com/javase/jp/8/docs/api/java/security/MessageDigest.htmlを参考
            switch(type){
                case "MD5":
                    try{
                        MessageDigest md5=MessageDigest.getInstance("MD5");
                        byte[] md5Byte=md5.digest(text.getBytes());
                        result.setText(String.format("%032x",new BigInteger(1, md5Byte)));//32桁に合わせる

                    }catch(Exception e){
                        JOptionPane.showMessageDialog(null,"エラーが発生しました");
                    }
                    break;

                case "ROT13":
                    result.setText(ROT13(text));
                    break;

                case "SHA-256":
                    try{
                        MessageDigest sha256=MessageDigest.getInstance("SHA-256");
                        byte[] sha256Byte=sha256.digest(text.getBytes());
                        result.setText(String.format("%064x",new BigInteger(1, sha256Byte)));//64桁に合わせる
                    }catch(Exception e){
                        JOptionPane.showMessageDialog(null, "エラーです");
                    }
                    break;
                
                case "To Base64"://参考[https://docs.oracle.com/javase/jp/8/docs/api/java/util/Base64.html#getEncoder--]
                    result.setText(Base64.getEncoder().encodeToString(text.getBytes()));
                    break;
                
                case "From Base64":
                    try{
                        result.setText(new String(Base64.getDecoder().decode(text.getBytes())));
                    }catch(Exception e){
                        JOptionPane.showMessageDialog(null, "形式を確認してください");
                    }
                    break;
            }
        }else if(file.isSelected()){//ファイルから生成
            String path=field.getText();
            boolean cantOpen=false;
            //ファイルのバイナリを読み込む方法[https://style.potepan.com/articles/17185.html]を参考
            try {
            	nakami=Files.readAllBytes(Paths.get(path));
            }catch(Exception e) {
            	JOptionPane.showMessageDialog(null, "ファイルを開けません");
            	cantOpen=true;
            }
            if(!cantOpen) {
            switch(type){
                
                case "MD5":
                    try{
                        MessageDigest md5=MessageDigest.getInstance("MD5");
                        byte[] md5Byte=md5.digest(nakami);
                        result.setText(String.format("%032x",new BigInteger(1, md5Byte)));//32桁に合わせる

                    }catch(Exception e){
                        JOptionPane.showMessageDialog(null,"エラーが発生しました");
                    }
                    break;
                case "SHA-256":
                    try{
                        MessageDigest sha256=MessageDigest.getInstance("SHA-256");
                        byte[] sha256Byte=sha256.digest(Files.readAllBytes(Paths.get(path)));
                        result.setText(String.format("%064x",new BigInteger(1, sha256Byte)));//64桁に合わせる
                    }catch(Exception e){
                        JOptionPane.showMessageDialog(null, "エラーが発生しました");
                    }
                    break;
                case "SHA512":
                    try{
                        MessageDigest sha512=MessageDigest.getInstance("SHA-512");
                        byte[] sha512Byte=sha512.digest(Files.readAllBytes(Paths.get(path)));
                        result.setText(String.format("%0128x",new BigInteger(1, sha512Byte)));//128桁に合わせる
                    }catch(Exception e){
                        JOptionPane.showMessageDialog(null, "エラーが発生しました");
                    }
                    break;
            }
            }
        }
        if(!checksum.getText().equals("")){
            if(result.getText().equals(checksum.getText())){
                JOptionPane.showMessageDialog(null, "チェックサムが一致しました");
            }else{
                JOptionPane.showMessageDialog(null, "チェックサムが一致しません");
            }
        }
    }

    //Copyボタンが押されたときの処理
    public void cop(){//クリップボードへのコピーに関する情報は[https://allabout.co.jp/gm/gc/80702/]から引用・参考
        StringSelection selection = new StringSelection(result.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    //ROT13の処理
    public String ROT13(String stt){
        //ROT13の処理
        String res="";
        boolean notEnglish=false;
        for(int i=0;i<stt.length();i++){
            char c=stt.charAt(i);
            if(('a'<=c&&c<='m')||('A'<=c&&c<='M')){
                c+=13;
            }
            else if(('n'<=c&&c<='z')||('N'<=c&&c<='Z')){
                c-=13;
            }else{
                notEnglish=true;
                c=c;
            }
            res+=c;
        }
        if(notEnglish){
            JOptionPane.showMessageDialog(null, "英語以外の文字が含まれているため該当文字はそのまま出力します");
        }
        return res;
    }
}