package com.studio.b56.im.app.ui;

import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.studio.b56.im.R;

public class RegisterProtocolActivity extends BaseRegisterActivity{
	@ViewInject(id = R.id.protocol_check_confirm)
	private CheckBox checkBoxConfirm;      // 是否同意注册协议
	@ViewInject(id = R.id.protocol_btn_confirm)
	private Button btnConfirm;				// 确定注册
	@ViewInject(id = R.id.protocol_btn_connel)
	private Button btnCannel;              // 取消注册
	
	@ViewInject(id = R.id.edit_protocol)
	private EditText editProtocol;
	
	private View.OnClickListener onClickListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.register_protocol);
		super.onCreate(savedInstanceState);
		
		addLisener();
		
		initProtocol();
	}

	private void initProtocol() {
		StringBuilder builder = new StringBuilder();
		builder.append("欢迎你加入成为艺讯网会员，艺讯网会员服务(以下称会员服务)系由‘艺讯网’(以下称本网站)所建置提供，所有申请使用会员服务之使用者(以下称会员)，都应该详细阅读下列使用条款，这些使用条款订立的目的，是为了保护会员服务的提供者以及所有使用者的利益，并构成使用者与会员服务提供者之间的契约，使用者完成注册手续、或开始使用伴玩中国网所提供之会员服务时，即视为已知悉、并完全同意本使用条款的所有约定：\n")
		.append("■会员服务\n")
		.append("1. 一旦本网站完成并确认你的申请后，本网站将提供予你的‘一般’会员服务内容或其他未来可能新增之一般会员服务。\n")
		.append("2. 会员服务之期间，是指使用者填妥申请表格并完成注册程序后，本网站于完成相关系统设定、使会员服务达于可供使用之日。\n")
		.append("3. 会员服务仅依当时所提供之功能及状态提供服务；本公司并保留新增、修改或取消会员服务内相关系统或功能之全部或一部之权利。\n")
		.append("■帐号、密码与安全性\n")
		.append("1. 在使用会员服务以前，必须经过完整的注册手续，在注册过程中你必须填入完整、而且正确的资料。\n")
		.append("2. 在注册过程中你可以自行选择使用者名称和密码，但在使用会员服务的过程中，你必须为经由这个使用者名称和密码所进行的所有行为负责。\n")
		.append("3. 对于你所取得的使用者名称和密码，你必须负妥善保管和保密的义务，如果你发现或怀疑这个使用者名称和密码被其他人冒用或不当使用，你必须立即通知本网站管理者，以利本网站尽快采取适当因应措施，但上述因应措施不得因此解释为本公司明示或默示对你负有任何形式之赔偿或补偿之责任或义务。\n")
		.append("3. 会员服务仅依当时所提供之功能及状态提供服务；本公司并保留新增、修改或取消会员服务内相关系统或功能之全部或一部之权利。\n")
		.append("■资料的隐私   \n")
		.append("1. 本网站会保护每一位会员的隐私，不管是申请帐号、个人资料、邮件内容、或所储存的网站资料，除了可能涉及违法、侵权、或违反使用条款、或经本人同意以外，本系统不会任意监视、增删、修改或关闭，或将个人资料及邮件内容交予第三者，包括赞助之广告厂商。\n")
		.append("2. 会员服务中如有部分特定服务是与其他合作厂商或伙伴共同经营，如果你不愿将个人资料揭露给其他合作伙伴，可以选择不使用这些特定服务，但如你开始使用这些特定服务，本公司将在资料搜集或传输前明确告知你后，将你的个人资料揭露给该特定服务之合作厂商或伙伴。。\n")
		.append("3. 在下列的情况下，本公司有可能会查看或提供你的个人或相关电信资料给有权机关、或主张其权利受侵害并提出适当证明之第三人：\n")
		.append("(1) 依法令规定、或依司法机关或其他有权机关的命令；\n")
		.append("(2) 为执行本使用条款、或使用者违反使用条款；\n")
		.append("(3) 为保护会员服务系统之安全或经营者之合法权益；\n")
		.append("(4) 为保护其他使用者或其他第三人的合法权益；\n")
		.append("(5) 为维护会员服务系统的正常运作。\n")
		.append("■使用者的行为 \n")
		.append("1. 必须遵守《全国人大常委会关于维护互联网安全的决定》及中华人民共和国其他有关法律法规。 \n")
		.append("2. 不得制作、复制、发布、传播含有下列内容的信息：\n")
		.append("(1) 反对宪法所确定的基本原则的；\n")
		.append("(2) 危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；\n")
		.append("(3) 损害国家荣誉和利益的；\n")
		.append("(4) 煽动民族仇恨、民族歧视，破坏民族团结的；\n")
		.append("(5) 破坏国家宗教政策，宣扬邪教和封建迷信的；\n")
		.append("(6) 散布谣言，扰乱社会秩序，破坏社会稳定的；\n")
		.append("(7) 散布淫秽、色情、赌博、暴力、凶杀、恐怖或者教唆犯罪的；\n")
		.append("(8) 侮辱或者诽谤他人，侵害他人合法权益的；\n")
		.append("(9) 含有法律、行政法规禁止的其他内容的。\n")
		.append("3. 任何未经事前授权的商业行为都是被禁止的。\n")
		.append("4. 你利在本站发布的任何信息，均仅代表你的个人观点，而与本公司无涉。\n")
		.append("5. 您承担一切因您的行为而直接或间接导致的民事或刑事法律责任。")
		.append("■服务暂停或中断\n")
		.append("1. 在下列情形，本网站将暂停或中断本服务之全部或一部，且对使用者任何直接或间接之损害，均不负任何责任：\n")
		.append("(1) 对本服务相关软硬体设备进行搬迁、更换、升级、保养或维修时；\n")
		.append("(2) 使用者有任何违反政府法令或本使用条款情形； \n")
		.append("(3) 天灾或其他不可抗力所致之服务停止或中断； \n")
		.append("(4) 其他不可归责于本网站之事由所致之服务停止或中断。 \n")
		.append("2. 会员服务系统或功能‘例行性’之维护、改置或变动所发生之服务暂停或中断，本网站将于该暂停或中断前以公告或其他适当之方式告知会员。 \n")
		.append("3. 因使用者违反相关法令或本使用条款、或依相关主管机关之要求、或因其他不可归责于本公司之事由，而致本服务全部或一部暂停或中断时，暂停或中断期间仍照常计费。\n")
		.append("■终止服务 \n")
		.append("1. 基于公司的运作，会员服务有可能停止提供服务之全部或一部，使用者不可以因此而要求任何赔偿或补偿。 \n")
		.append("2. 如果你违反了本使用条款，本网站保留随时暂时停止提供服务、或终止提供服务之权利，你不可以因此而要求任何赔偿或补偿。 \n")
		.append("3. 如果你在会员服务上所刊载、传输、发送或储存的档案或资料，有任何违反法令、违反本使用条款、有侵害第三人权益之虞，本网站保留随时得不经通知直接加以移动、删除或停止服务提供之权利。若本网站因此受到任何损害，你应对本网站负损害赔偿之责。 \n")
		.append("■修改本使用条款的权利 \n")
		.append("本网站保留随时修改本会员服务使用规则的权利，修改后的会员服务使用条款将公布在本网站上，不另外个别通知使用者。 \n")
		.append("本约定条款解释、补充及适用均以中华人民共和国法令依据。 \n")
		;
		
		editProtocol.setText(builder.toString());
		
	}

	private void addLisener() {
		// TODO Auto-generated method stub
		onClickListener = new RegisterProtocolOnClickLisener();
		btnConfirm.setOnClickListener(onClickListener);
		btnCannel.setOnClickListener(onClickListener);
	}

	@Override
	protected void initTitle() {
		
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	// 同意
	void  confirm(){
		boolean confirmState = checkBoxConfirm.isChecked();
		
		if(confirmState){
			Intent intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
		}else{
			Toast.makeText(getBaseContext(), getResources().getString(R.string.register_protocol_confirm_toast), Toast.LENGTH_SHORT).show();
		}
	}
	// 不同意
	void  cannel(){
		finish();
	}
	
	class RegisterProtocolOnClickLisener implements View.OnClickListener{

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.protocol_btn_confirm:
				confirm();
				break;
			
			case R.id.protocol_btn_connel:
				cannel();
				break;
				
			default:
				break;
			}
			
		}
		
	}

}
