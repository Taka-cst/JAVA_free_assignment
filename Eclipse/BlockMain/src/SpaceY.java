//SpaceY.java
//課題
//エンジンの使用上限を設けた
//工夫点
//エンジンの上限と現在の使用量から使用率を表示するようにした
//使用率をプログレスバーとして画面下に表示するようにした
//燃料がなくなるとクリックしてもエンジン点火の絵が表示されないようにした
//燃料がなくなると燃料切れと表示されるようにした
//着陸成功or失敗した場合にダイアログで結果を表示し、もう一度プレイするかどうかを選択できるようにした
//着陸成功後、再度遊ぶ場合は0.9倍のエンジン上限とした

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import javax.swing.JProgressBar;
import java.awt.BorderLayout;

//JOption
import javax.swing.JOptionPane;

public class SpaceY extends JFrame{
	int ground = 60; // 地面
    double fullengine=100;//燃料
    double engine=fullengine;
    Boolean nokori=true;
	JProgressBar pb;

	public static void main(String[] args) {
		SpaceY window = new SpaceY();
		window.setTitle("SpaceY");
		window.setSize(300, 600);
		window.setVisible(true);
	}
	
	public SpaceY() {
		setLayout(new BorderLayout());
		pb=	new JProgressBar(0,100);
		pb.setValue(100);
		add(pb, BorderLayout.SOUTH);
		RocketCanvas canvas = new RocketCanvas();
		add(canvas, BorderLayout.CENTER);
	}

	public class RocketCanvas extends Canvas implements Runnable{
		Thread thread;
		Rocket rocket;
		public RocketCanvas() {
			rocket =new Rocket();
			addMouseListener(rocket);
			thread = new Thread(this);
			thread.start();
		}
		
		public void paint(Graphics g) {
			Dimension dim = getSize(); // 描画領域の大きさを調べる
			// ダブルバッファリング
			Image m_image = createImage(dim.width, dim.height);// 裏画面の画像イメージ
			Graphics m_g = m_image.getGraphics(); // 裏画面に付随するグラフィックスオブジェクト

			m_g.setColor(Color.cyan);
			m_g.fillRect(0, 0, dim.width, dim.height - ground);
			m_g.setColor(Color.white);
			m_g.fillRect(0, dim.height -ground, dim.width, ground);
			rocket.draw(m_g, dim);

			m_g.setColor(Color.black);
			//m_g.drawString("Score: " + score, 10, 20);
			
			g.drawImage(m_image, 0, 0, this); // 裏画面を表画面にコピー
		}
		
		@Override
		public void run() {
			while(true) {
				Dimension dim = getSize();
				try {Thread.sleep(50);}
				catch(InterruptedException e) {}
				if(dim.height == 0) continue;
				rocket.move(dim);
				double per=engine/fullengine*100;
				pb.setValue((int)per);
				repaint();
				if(rocket.landing||rocket.crush){
				if(rocket.landing){
					JOptionPane.showMessageDialog(this, "着陸成功");
					
				}else if(rocket.crush){
					JOptionPane.showMessageDialog(this, "着陸失敗");
				}
				int result = JOptionPane.showConfirmDialog(this, "もう一度プレイしますか？", "確認", JOptionPane.YES_NO_OPTION);
					if(result == JOptionPane.YES_OPTION){
						if(rocket.landing) fullengine*=0.9;
						rocket = new Rocket();
						addMouseListener(rocket);
						engine=fullengine;
						nokori=true;
					}else{
						System.exit(0);
					}
				}
				
			}
		}
	}
		
	public class Rocket implements MouseListener{
		double vy = 0;
		static final double V = 5.0; // 着陸成功速度の上限
		static final double G =  0.5; // 重力加速度
		static final double J = -1.0; // ロケット推進力(加速度)
		boolean inplay = true;
		boolean jet = false; // true: エンジン噴射あり, false: エンジン噴射無し
		boolean crush = false; // true: 着陸失敗
		boolean landing = false; // true: 着陸成功
		int width, height;
		int x, y = 10;
		int radius = 10;

		public void draw(Graphics m_g, Dimension dim) {
			width = dim.width;
			height = dim.height;
			x = width / 2;
			m_g.setColor(Color.blue);
			m_g.fillOval(x - radius, y - radius, 2*radius, 2*radius);
			if (jet && nokori) {
				m_g.setColor(Color.orange);
				m_g.fillOval(x - radius / 2, y + radius, radius, 2*radius);
			}
            double per=engine/fullengine*100;
            System.out.println("残り"+per);
			m_g.setColor(Color.black);
			m_g.drawString("速度"+V+"以下で着陸成功", 10, height -45);
			m_g.drawString("速度 = "+vy+"   残り燃料"+(int)per+"%", 10, height -25);
            System.out.println(engine);
            if(engine<1 && !(landing||crush)){
                System.out.println("燃料切れ");
                nokori=false;
                m_g.drawString("燃料切れ(T_T)", width /2, height / 2);
            }
			if (landing) m_g.drawString("着陸成功!", width /2, height / 2);
			if (crush) m_g.drawString("着陸失敗!", width /2, height / 2);
		}

		public void move(Dimension dim) {
			if (!inplay) return; // ゲーム終了なら動かさない
			vy += G;
			if (jet&&nokori){
                vy += J;
                engine--;
            }
            y += (int)vy;
			if (y  >= dim.height - ground - radius ) {
				if (vy < V) 
					landing = true;
				else
					crush = true;
				inplay = false; // ゲーム終了
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) { }

		@Override
		public void mousePressed(MouseEvent e) {
		    jet = true; // エンジン噴射
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			jet = false; // エンジンオフ
		}

		@Override
		public void mouseEntered(MouseEvent e) { }

		@Override
		public void mouseExited(MouseEvent e) { }
	}
}