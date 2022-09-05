package gui;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.util.Date;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.apache.log4j.BasicConfigurator;

import data.Person;
import helper.XMLConvert;

public class FrmSend extends JFrame implements ActionListener{
	private Button btn;
	private JTextField txtManv;
	public FrmSend() {
		setTitle("Gửi Thông tin");
		setSize(900, 600);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		buildUI();
	}
	
	public void buildUI() {
		JPanel jpanelTieude = new JPanel();
		JLabel lblManv = new JLabel("Nhập Thông tin");
		txtManv = new JTextField(72);
		btn = new Button("Gửi");
		jpanelTieude.add(lblManv);
		jpanelTieude.add(txtManv);
		jpanelTieude.add(btn);
		add(jpanelTieude,BorderLayout.CENTER);
		btn.addActionListener(this);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		
		if(o.equals(btn)) {
			
			try {
				QueueSender();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			FrmTake frmTake = new FrmTake();
			frmTake.setVisible(true);
		}
		


		
	}
	public void QueueSender()  throws Exception{
		 
	//config environment for JMS
			BasicConfigurator.configure();
	//config environment for JNDI
			Properties settings = new Properties();
			settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
			settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
	//create context
			Context ctx = new InitialContext(settings);
	//lookup JMS connection factory
			ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
	//lookup destination. (If not exist-->ActiveMQ create once)
			Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
	//get connection using credential
			Connection con = factory.createConnection("admin", "admin");
	//connect to MOM
			con.start();
	//create session
			Session session = con.createSession(/* transaction */false, /* ACK */Session.AUTO_ACKNOWLEDGE);
	//create producer
			MessageProducer producer = session.createProducer(destination);
	//create text message
			Message msg = session.createTextMessage(txtManv.getText());
			producer.send(msg);
			Person p = new Person(1001, "Thân Thị Đẹt", new Date());
			String xml = new XMLConvert<Person>(p).object2XML(p);
			msg = session.createTextMessage(xml);
			producer.send(msg);
	//shutdown connection
			session.close();
			con.close();
			System.out.println("Finished...");
	}
	


}
