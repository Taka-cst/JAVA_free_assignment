/**************************************************/
/*セキュリティラボではサークルメンバーを募集しています。/
/**************************************************/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.security.*;
import javax.swing.JOptionPane;

import java.util.Base64;

//クリップボードにコピーする
import java.awt.datatransfer.*;

import java.math.BigInteger;
import java.io.File;
import java.io.FileInputStream;

public class SHAFree extends JFrame implements ActionListener{
    JLabel label1,label2,label3;
    ButtonGroup group;
    JTextField field,checksum;
    JRadioButton mozi,file;
    JButton generate,copy;//生成ボタンとコピーボタン
    JTextArea result;//生成結果や比較結果の表示
    JComboBox comboBox;


    String[] FromText = {"MD5", "ROT13", "SHA-256","To Base64", "From Base64"};
    String[] FromFile = {"MD5", "SHA-256"};
    public static void main(String[] args){
        SHAFree w = new SHAFree();
        w.setTitle("SHAジェネレータ");
        w.setSize(520, 300);
        w.setVisible(true);
    }

    public SHAFree(){
        setLayout(new BorderLayout()); // BorderLayoutを設定
        //ラベル類
        JLabel setumei;
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
        generate= new JButton("生成/比較");
        copy= new JButton(String.format("結果\nコピー"));
        setumei= new JLabel("SHAジェネレータ");
        add(setumei,BorderLayout.NORTH);
        //配置
        JPanel panel1=new JPanel(new GridLayout(3, 2));
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
        resultLabel.setHorizontalAlignment(JLabel.CENTER);
        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultPanel.add(result, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.CENTER);

        // add(result, BorderLayout.CENTER);
        JPanel panel2 = new JPanel();
        panel2.add(generate);
        panel2.add(copy);
        add(panel2, BorderLayout.SOUTH);

        //アクションパフォームド追加
        mozi.addActionListener(this);
        file.addActionListener(this);
        generate.addActionListener(this);
        copy.addActionListener(this);
    }

    public void actionPerformed(ActionEvent ae){
        if(ae.getSource()==mozi){
            comboBox.setModel(new DefaultComboBoxModel(FromText));//参考[1]
        }else if(ae.getSource()==file){
            comboBox.setModel(new DefaultComboBoxModel(FromFile));
        }else if(ae.getSource()==generate){
            gen();
        }else if(ae.getSource()==copy){
            cop();
        }
    }

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
                        JOptionPane.showMessageDialog(null, "エラーが発生しました");
                    }
                    break;
                
                case "To Base64":
                    result.setText(Base64.getEncoder().encodeToString(text.getBytes()));
                    break;
                
                case "From Base64":
                    result.setText(new String(Base64.getDecoder().decode(text.getBytes())));
                    break;
            }
        }else if(file.isSelected()){//ファイルから生成
            String path=field.getText();
            switch(type){
                case "MD5":
                    try{
                        MessageDigest md5=MessageDigest.getInstance("MD5");
                        FileInputStream fileinput=new FileInputStream(new File(path));
                        byte[] md5Byte=md5.digest(fileinput.readAllBytes());
                        result.setText(String.format("%032x",new BigInteger(1, md5Byte)));//32桁に合わせる

                    }catch(Exception e){
                        JOptionPane.showMessageDialog(null,"エラーが発生しました");
                    }
                    break;
                case "SHA-256":
                    try{
                        MessageDigest sha256=MessageDigest.getInstance("SHA-256");
                        FileInputStream fileinput=new FileInputStream(new File(path));
                        byte[] sha256Byte=sha256.digest(fileinput.readAllBytes());
                        result.setText(String.format("%064x",new BigInteger(1, sha256Byte)));//64桁に合わせる
                    }catch(Exception e){
                        JOptionPane.showMessageDialog(null, "エラーが発生しました");
                    }
                    break;
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

    public void cop(){//クリップボードへのコピーに関する情報は[https://allabout.co.jp/gm/gc/80702/]から引用・参考
        StringSelection selection = new StringSelection(result.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

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