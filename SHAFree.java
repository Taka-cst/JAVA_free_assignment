import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.security.*;

public class SHAFree extends JFrame {
    JLabel label1,label2,label3;
    ButtonGroup group;
    JTextField field,checksum;
    JRadioButton mozi,file;
    JButton generate,copy;//生成ボタンとコピーボタン
    JTextArea result;//生成結果や比較結果の表示
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
        mozi=new JRadioButton("文字から生成");
        file=new JRadioButton("ファイルから生成");
        group=new ButtonGroup();
        group.add(mozi);
        group.add(file);
        label1 = new JLabel("生成の種類:");
        label2 = new JLabel("元のテキスト/ファイルパス:");
        label3 = new JLabel("チェックサム(任意)");
        field = new JTextField(30);
        checksum = new JTextField(30);
        result = new JTextArea(10, 40);
        result.setLineWrap(true);
        result.setWrapStyleWord(true);
        generate = new JButton("生成");
        copy = new JButton(String.format("結果\nコピー"));
        setumei = new JLabel("SHAジェネレータ");
        add(setumei,BorderLayout.NORTH);
        //配置
        JPanel panel1 = new JPanel(new GridLayout(3, 2));
        JPanel panel1_1 = new JPanel(new GridLayout(1, 2));
        panel1_1.add(mozi);
        panel1_1.add(file);
        panel1.add(panel1_1);

        JComboBox<String> comboBox = new JComboBox<>(new String[]{"SHA-1", "SHA-256", "SHA-512"});
        panel1.add(comboBox);
        panel1.add(label2);
        panel1.add(field);
        panel1.add(label3);
        panel1.add(checksum);
        add(panel1, BorderLayout.NORTH);
        add(new JScrollPane(result), BorderLayout.CENTER);
        JPanel panel2 = new JPanel();
        panel2.add(generate);
        panel2.add(copy);
        add(panel2, BorderLayout.SOUTH);

    }
}