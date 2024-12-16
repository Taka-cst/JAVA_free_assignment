import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.security.*;

public class SHAFree extends JFrame implements ActionListener {
    JLabel label;
    JTextField field;
    public static void main(String[] args){
        SHAFree w = new SHAFree();
        w.setTitle("SHAジェネレータ");
        w.setSize(400, 300);
        w.setVisible(true);
    }

    public SHAFree(){
        setLayout(new BorderLayout());
        label = new JLabel("テキストを入力してください");
        add(label, BorderLayout.NORTH);
        field = new JTextField();
        add(field, BorderLayout.CENTER);
        field.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource()==field){
            try{
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(field.getText().getBytes());
                byte[] result = md.digest();
                StringBuffer buf = new StringBuffer();
                for(int i=0; i<result.length; i++){
                    buf.append(String.format("%02x", result[i]));
                }
                label.setText(buf.toString());
            }catch(Exception e){
                label.setText("エラーが発生しました");
            }
        }
    }

}