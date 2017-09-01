package com.eb.sc.priter;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.eb.sc.R;
import com.eb.sc.scanner.BaseActivity;
import com.eb.sc.scanner.ExecutorFactory;
import com.eb.sc.utils.FileUtil;
import com.eb.sc.utils.SupportMultipleScreensUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PrinterActivity extends BaseActivity implements OnClickListener, OnCheckedChangeListener, AdapterView.OnItemSelectedListener{
	public static final String TAG = "PrinterActivity";
	private Button btnQrCode, btnBarCode, btnPrintPic, btnPrint, btnUnicode, btn_wordToPic,
			btnSelectPic, btn_normal, btn_vAmaplify, btn_hAmaplify, btn_amaplify,
			btn_wordAlginLeft, btn_wordAlginMiddle, btn_wordAlginRight,
			btn_picAlginLeft, btn_picAlginMiddle, btn_picAlginRight,
			btnPrintModelOne, btnPrintModelTwo, btnPrintModelThree, btnSuperPrinter,
	        btnPrintPicGray, btnPrintPicRaster, btnPrintUnicode1F30;
	private TextView tv_printStatus, tv_printer_soft_version;
	private EditText et_printText;
	private ImageView iv_printPic;
	private Bitmap mBitmap = null;
	private CheckBox mAutoOutputPaper;
	RadioGroup rg_fontGroup;
	private static final int REQUEST_EX = 1;
	private int fontType = 0;

	private String printTextString = "";

	@Override
	protected void onStop() {
		super.onStop();
	}

	private boolean runFlag = true;
	//标签打印标记
	private boolean autoOutputPaper = false;
	String text;

	private Spinner spinnerLanguage, spinner_pic_style;
	private boolean SCREEN_ON = false;
    /**
     * 图片打印类型
     */
    int imageType=0;
    final String[] imageTypeArray=new String[]{"POINT","GRAY","RASTER"};
	ScreenOnOffReceiver mReceiver = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_printer);
		View rootView=findViewById(android.R.id.content);
		SupportMultipleScreensUtil.init(getApplication());
		SupportMultipleScreensUtil.scale(rootView);
		initView();
//		mReceiver = new ScreenOnOffReceiver();
//		IntentFilter screenStatusIF = new IntentFilter();
//		screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
//		screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF);
//		registerReceiver(mReceiver, screenStatusIF);
		enableOrDisEnableKey(false);
		ExecutorFactory.executeThread(new Runnable() {
			@Override
			public void run() {
				while(runFlag){
					if(bindSuccessFlag){
						//检测打印是否正常
						try {
							mIzkcService.printerInit();
							String printerSoftVersion = mIzkcService.getFirmwareVersion1();
							Log.i("gggg","printerSoftVersion="+printerSoftVersion);
							if(TextUtils.isEmpty(printerSoftVersion)){
								printerSoftVersion = mIzkcService.getFirmwareVersion2();
								Log.i("gggg","printerSoftVersion1="+printerSoftVersion);
							}
							if(TextUtils.isEmpty(printerSoftVersion)){
								mIzkcService.setModuleFlag(0);
								mHandler.obtainMessage(1).sendToTarget();
								Log.i("gggg","mIzkcService=");
							}else{
								mHandler.obtainMessage(0, printerSoftVersion).sendToTarget();
								Log.i("gggg","obtainMessage");
								runFlag = false;
							}
						} catch (RemoteException e) {
							e.printStackTrace();
							Log.i("gggg","runFlag="+e.toString());
						}

					}
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		//查询服务是否绑定成功，bindSuccessFlag为服务是否绑定成功的标记，在BaseActivity声
//		runFlag = true;
//		enableOrDisEnableKey(false);

	}

	Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				enableOrDisEnableKey(true);
				generateBarCode();
				String status;

				String aidlServiceVersion;
				try {
//					mIzkcService.sendRAWData("printer", new byte[] {0x1b, 0x40});
					status = mIzkcService.getPrinterStatus();
					mIzkcService.setModuleFlag(0);
					tv_printStatus.setText(status);
					Log.i("gggg","status="+status);
					aidlServiceVersion = mIzkcService.getServiceVersion();
					tv_printer_soft_version.setText(msg.obj + "AIDL Service Version:" + aidlServiceVersion);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 1:
				Toast.makeText(PrinterActivity.this, "正在连接打印机，请稍后...", Toast.LENGTH_SHORT).show();
				break;
			case 8:
//				showProgressDialog("waiting...");
//				new Timer().schedule(new TimerTask() {
//					@Override
//					public void run() {
//						dismissLoadDialog();
//					}
//				}, 8000);
				break;
			default:
				break;
			}
			return false;
		}
	});


	private void initView() {
		btnBarCode = (Button) findViewById(R.id.btnBarCode);
		btnBarCode.requestFocus();
		btnQrCode = (Button) findViewById(R.id.btnQrCode);
		btnPrintPic = (Button) findViewById(R.id.btnPrintPic);
		btnPrint = (Button) findViewById(R.id.btnPrint);
		btnUnicode = (Button) findViewById(R.id.btnUnicode);
		btn_wordToPic = (Button) findViewById(R.id.btn_wordToPic);
		btnSelectPic = (Button) findViewById(R.id.btnSelectPic);
		btn_normal = (Button) findViewById(R.id.btn_normal);
		btn_amaplify = (Button) findViewById(R.id.btn_amplify);
		btn_vAmaplify = (Button) findViewById(R.id.btn_vAmaplify);
		btn_hAmaplify = (Button) findViewById(R.id.btn_hAmplify);
		rg_fontGroup = (RadioGroup) findViewById(R.id.rg_fontGroup);
		btn_wordAlginLeft = (Button) findViewById(R.id.btn_wordAlginLeft);
		btn_wordAlginMiddle = (Button) findViewById(R.id.btn_wordAlginMiddle);
		btn_wordAlginRight = (Button) findViewById(R.id.btn_wordAlginRight);
		btn_picAlginLeft = (Button) findViewById(R.id.btn_picAlginLeft);
		btn_picAlginMiddle = (Button) findViewById(R.id.btn_picAlginMiddle);
		btn_picAlginRight = (Button) findViewById(R.id.btn_picAlginRight);
		
		btnPrintModelOne = (Button) findViewById(R.id.btnPrintModelOne);
		btnPrintModelTwo = (Button) findViewById(R.id.btnPrintModelTwo);
		btnPrintModelThree = (Button) findViewById(R.id.btnPrintModelThree);
		btnSuperPrinter = (Button) findViewById(R.id.btnSuperPrinter);
		btnPrintPicGray = (Button) findViewById(R.id.btnPrintPicGray);
		btnPrintPicRaster = (Button) findViewById(R.id.btnPrintPicRaster);
		btnPrintUnicode1F30 = (Button) findViewById(R.id.btnPrintUnicode1F30);
		tv_printer_soft_version = (TextView) findViewById(R.id.tv_printer_soft_version);
		spinnerLanguage = (Spinner) findViewById(R.id.spinner_language);
		spinner_pic_style = (Spinner) findViewById(R.id.spinner_pic_style);

		tv_printStatus = (TextView) findViewById(R.id.tv_printStatus);
		et_printText = (EditText) findViewById(R.id.et_printText);
		iv_printPic = (ImageView) findViewById(R.id.iv_printPic);
		et_printText.setText("这是我打印出来的文字\\r\\nabcdefghijklmnopkrstuvwxyz1234567890");
		text= et_printText.getText().toString()+"\n";
		et_printText.setSelection(et_printText.getText().toString().length());
		mAutoOutputPaper = (CheckBox) findViewById(R.id.cb_auto_out_paper);
		mAutoOutputPaper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					autoOutputPaper = true;
				}else{
					autoOutputPaper = false;
				}
			}
		});

		SpinnerAdapterLanguage adapter = new SpinnerAdapterLanguage(this, android.R.layout.simple_spinner_item, getData());
		spinnerLanguage.setAdapter(adapter);
		spinnerLanguage.setOnItemSelectedListener(this);
        spinner_pic_style.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,imageTypeArray));
        spinner_pic_style.setOnItemSelectedListener(this);
		spinner_pic_style.setSelection(0);
		initEvent();
	}

	/**
	 * 构造SimpleAdapter的第二个参数，类型为List<Map<?,?>>
	 * @param
	 * @return
	 */
	private List<LanguageModel> getData() {
		Resources res =getResources();
		String[] cmdStr=res.getStringArray(R.array.language);
		List<LanguageModel> languageModelList=new ArrayList<>();
		for(int i=0;i<cmdStr.length;i++)
		{
			String [] cmdArray=cmdStr[i].split(",");
			if(cmdArray.length==3)
			{
				LanguageModel languageModel=new LanguageModel();
				languageModel.code=Integer.parseInt(cmdArray[0]);
				languageModel.language=cmdArray[1];
				languageModel.description=cmdArray[1]+" "+cmdArray[2];
				languageModelList.add(languageModel);
			}
		}
		return languageModelList;
	}

	private void initEvent() {
		btnBarCode.setOnClickListener(this);
		btnQrCode.setOnClickListener(this);
		btnPrintPic.setOnClickListener(this);
		btnPrint.setOnClickListener(this);
		btnUnicode.setOnClickListener(this);
		btnSelectPic.setOnClickListener(this);
		btn_wordToPic.setOnClickListener(this);
		btn_normal.setOnClickListener(this);
		btn_vAmaplify.setOnClickListener(this);
		btn_hAmaplify.setOnClickListener(this);
		btn_amaplify.setOnClickListener(this);
		rg_fontGroup.setOnCheckedChangeListener(this);
		btn_wordAlginLeft.setOnClickListener(this);
		btn_wordAlginMiddle.setOnClickListener(this);
		btn_wordAlginRight.setOnClickListener(this);
		btn_picAlginLeft.setOnClickListener(this);
		btn_picAlginMiddle.setOnClickListener(this);
		btn_picAlginRight.setOnClickListener(this);
		btnPrintModelOne.setOnClickListener(this);
		btnPrintModelTwo.setOnClickListener(this);
		btnPrintModelThree.setOnClickListener(this);
		btnSuperPrinter.setOnClickListener(this);
		btnPrintUnicode1F30.setOnClickListener(this);
		btnPrintPicRaster.setOnClickListener(this);
		btnPrintPicGray.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnBarCode:
			generateBarCode();
			break;
		case R.id.btnQrCode:
			generateQrCode();
			break;
		case R.id.btn_wordToPic:
			wordToPic();
			break;
		case R.id.btnPrintPic:
			printPic();
			break;
		case R.id.btnPrint:
			printGBKText();
			break;
		case R.id.btnUnicode:
			printUnicode();
			break;
		case R.id.btnSelectPic:
			selectPic();
			break;
		case R.id.btn_normal:
			printVamplify(0);
			break;
		case R.id.btn_vAmaplify:
			printVamplify(1);
			break;
		case R.id.btn_hAmplify:
			printVamplify(2);
			break;
		case R.id.btn_amplify:
			printVamplify(3);
			break;
		case R.id.btn_wordAlginLeft:
			printTextAlgin(0);
			break;
		case R.id.btn_wordAlginMiddle:
			printTextAlgin(1);
			break;
		case R.id.btn_wordAlginRight:
			printTextAlgin(2);
			break;
		case R.id.btn_picAlginLeft:
			printBitmapAlgin(0);
			break;
		case R.id.btn_picAlginMiddle:
			printBitmapAlgin(1);
			break;
		case R.id.btn_picAlginRight:
			printBitmapAlgin(2);
			break;
		case R.id.btnPrintModelOne:
			printPurcase(false, false);
			break;
		case R.id.btnPrintModelTwo:
			printPurcase(true, false);
			break;
		case R.id.btnPrintModelThree:
			printPurcase(true, true);
			break;
		case R.id.btnSuperPrinter:
			superPrint();
			break;
		case R.id.btnPrintPicGray:
			printBitmapGray();
			break;
		case R.id.btnPrintPicRaster:
			printBitmapRaster();
			 break;
		case R.id.btnPrintUnicode1F30:
			printBitmapUnicode1F30();
			break;
		default:
			break;
		}
	}

	private void enableOrDisEnableKey(boolean enable){
		btnPrint.setEnabled(enable);
		btnUnicode.setEnabled(enable);
		btnPrintPic.setEnabled(enable);
		btnPrintModelOne.setEnabled(enable);
		btnPrintModelTwo.setEnabled(enable);
		btnPrintModelThree.setEnabled(enable);
		spinner_pic_style.setEnabled(enable);
		spinnerLanguage.setEnabled(enable);
	}

	private void printBitmapUnicode1F30() {
		text= et_printText.getText().toString()+"\n";
		try {
			mIzkcService.printUnicode_1F30(text);
			if(autoOutputPaper){
				mIzkcService.generateSpace();
			}
		} catch (RemoteException e) {
			Log.e("", "远程服务未连接...");
			e.printStackTrace();
		}
	}

	private void printBitmapRaster() {
		try {
			if(mBitmap!=null){
				mIzkcService.printRasterImage(mBitmap);
				if(autoOutputPaper){
					mIzkcService.generateSpace();
				}
			}
		} catch (RemoteException e) {
			Log.e("", "远程服务未连接...");
			e.printStackTrace();
		}
	}

	private void printBitmapGray() {
		try {
			if(mBitmap!=null){
				mIzkcService.printImageGray(mBitmap);
				if(autoOutputPaper){
					mIzkcService.generateSpace();
				}
			}
		} catch (RemoteException e) {
			Log.e("", "远程服务未连接...");
			e.printStackTrace();
		}
	}

	private void superPrint() {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + File.separator + "printData.txt";
		//读取模板数据，按行保存
		File file = new File(path);
		String[] contents;
		String content="";
		String new_content="";
		if(file.exists()){
			//获取文件内容
			content = FileUtil.convertCodeAndGetText(file);
			tv_printStatus.setText(content);
			Log.e("data", content);
		}else{
			Toast.makeText(this,"打印模板不存在", Toast.LENGTH_SHORT).show();
			return;
		}
		//匹配
		if(content.contains(PatternMatcher.SHOP_NAME)){
			//替换
			new_content = content.replace(PatternMatcher.SHOP_NAME, "智谷电");
		}if(content.contains(PatternMatcher.CASHIER_NAME)){
			new_content =new_content.replace(PatternMatcher.CASHIER_NAME, "林XX");
		} if(content.contains(PatternMatcher.TABLE_NAME)){
			new_content =new_content.replace(PatternMatcher.TABLE_NAME, "15");
		}if(content.contains(PatternMatcher.ORDER_NAME)){
			new_content =new_content.replace(PatternMatcher.ORDER_NAME, "576323258");
		}if(content.contains(PatternMatcher.ORDER_TIME)){
			new_content =new_content.replace(PatternMatcher.ORDER_TIME, "2017年4月1日17:51:06");
		}if(content.contains(PatternMatcher.GOODS_NAME)){
			new_content =new_content.replace(PatternMatcher.GOODS_NAME, "和田大枣");
		}if(content.contains(PatternMatcher.UNIT_PRICE)){
			new_content =new_content.replace(PatternMatcher.UNIT_PRICE, "12￥");
		}if(content.contains(PatternMatcher.SUB_TOTAL)){
			new_content =new_content.replace(PatternMatcher.SUB_TOTAL, "1200￥");
		}if(content.contains(PatternMatcher.GOODS_COUNT)){
			new_content =new_content.replace(PatternMatcher.GOODS_COUNT, "100");
		}if(content.contains(PatternMatcher.TOTAL_PRICE)){
			new_content =new_content.replace(PatternMatcher.TOTAL_PRICE, "1200￥");
		}if(content.contains(PatternMatcher.TOTAL_COUNT)){
			new_content =new_content.replace(PatternMatcher.TOTAL_COUNT, "12");
		}if(content.contains(PatternMatcher.CAN_RECEIVER)){
			new_content =new_content.replace(PatternMatcher.CAN_RECEIVER, "1200￥");
		}if(content.contains(PatternMatcher.PAY)){
			new_content =new_content.replace(PatternMatcher.PAY, "1200￥");
		}if(content.contains(PatternMatcher.REAL_RECEIVER)){
			new_content =new_content.replace(PatternMatcher.REAL_RECEIVER, "1200￥");
		}if(content.contains(PatternMatcher.CHARGE)){
			new_content =new_content.replace(PatternMatcher.CHARGE, "0.0￥");
		}if(content.contains(PatternMatcher.VIP_NAME)){
			new_content =new_content.replace(PatternMatcher.VIP_NAME, "李XX");
		}if(content.contains(PatternMatcher.VIP_NUMBER)){
			new_content =new_content.replace(PatternMatcher.VIP_NUMBER, "1111111");
		}if(content.contains(PatternMatcher.BLANCE)){
			new_content =new_content.replace(PatternMatcher.BLANCE, "77￥");
		}if(content.contains(PatternMatcher.INTEGRAL)){
			new_content =new_content.replace(PatternMatcher.INTEGRAL, "1300");
		}if(content.contains(PatternMatcher.SHOP_ADDRESS)){
			new_content =new_content.replace(PatternMatcher.SHOP_ADDRESS, "桃源居家乐福");
		}if(content.contains(PatternMatcher.ITEM)){
			new_content =new_content.replace(PatternMatcher.ITEM, "");
		}
		tv_printStatus.setText(new_content);
		//打印
		try {
			mIzkcService.printGBKText(new_content);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	private void printPurcase(boolean hasStartPic, boolean hasEndPic) {
		SupermakerBill bill = PrinterHelper.getInstance(this).getSupermakerBill(mIzkcService, hasStartPic, hasEndPic);
		PrinterHelper.getInstance(this).printPurchaseBillModelOne(mIzkcService,bill, imageType);
	}
	private void printBitmapAlgin(int alginStyle) {
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.logo);
//		Bitmap bitmap1 = resizeImage(bitmap, 376, 120);
		try {
			mIzkcService.printBitmapAlgin(bitmap, 376, 120, alginStyle);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void printTextAlgin(int alginStyle) {
		String pString = "智能打印\n";
		try {
			mIzkcService.printTextAlgin( pString, 0, 1, alginStyle);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void printVamplify(int type) {
		try {
			mIzkcService.printTextWithFont(text, fontType, type);
		} catch (RemoteException e) {
			Log.e("", "远程服务未连接...");
			e.printStackTrace();
		}
		
	}


	private void printFont(int type) {
		try {
//			mIzkcService.setTypeface(type);
//			mIzkcService.printGBKText(text);
			mIzkcService.printTextWithFont(text, type, 0);
		} catch (RemoteException e) {
			Log.e("", "远程服务未连接...");
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_EX && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			iv_printPic.setImageURI(selectedImage);
			mBitmap = BitmapFactory.decodeFile(picturePath);
			iv_printPic.setImageBitmap(mBitmap);
			if (mBitmap.getHeight() > 384) {
				iv_printPic.setImageBitmap(resizeImage(mBitmap, 384, 384));

			}
			cursor.close();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
		Bitmap BitmapOrg = bitmap;
		int width = BitmapOrg.getWidth();
		int height = BitmapOrg.getHeight();
		int newWidth = w;
		int newHeight = h;

		if (width >= newWidth) {
			float scaleWidth = ((float) newWidth) / width;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleWidth);
			Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
					height, matrix, true);
			return resizedBitmap;
		} else {
			Bitmap bitmap2 = Bitmap.createBitmap(newWidth, newHeight,
					bitmap.getConfig());
			Canvas canvas = new Canvas(bitmap2);
			canvas.drawColor(Color.WHITE);

			canvas.drawBitmap(BitmapOrg, (newWidth - width) / 2, 0, null);

			return bitmap2;
		}
	}

	private void selectPic() {
		Intent intent = new Intent(Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, REQUEST_EX);

	}

	private void generateQrCode() {
		try {
			mBitmap = mIzkcService.createQRCode("http://www.sznewbest.com", 384, 384);
			iv_printPic.setImageBitmap(mBitmap);
		} catch (RemoteException e) {
			Log.e("", "远程服务未连接...");
			e.printStackTrace();
		}
	}
	private void generateBarCode() {
		try {
			mBitmap = mIzkcService.createBarCode("4333333367", 1, 384, 120, true);
			iv_printPic.setImageBitmap(mBitmap);
		} catch (RemoteException e) {
			Log.i("gggg", "远程服务未连接...");
			e.printStackTrace();
		}
	}

	private void printUnicode() {
		text= et_printText.getText().toString()+"\n";
		try {
			mIzkcService.printUnicodeText(text);
			if(autoOutputPaper){
				mIzkcService.generateSpace();
			}
		} catch (RemoteException e) {
			Log.e("", "远程服务未连接...");
			e.printStackTrace();
		}
	}

	private void printGBKText() {
		text= et_printText.getText().toString()+"\n";
		try {
			mIzkcService.printGBKText(text);
			if(autoOutputPaper){
				mIzkcService.generateSpace();
			}
		} catch (RemoteException e) {
			Log.e("", "远程服务未连接...");
			e.printStackTrace();
		}
	}

	private void printPic() {
		try {
			if(mBitmap!=null){
				switch (imageType){
                    case 0:
                        mIzkcService.printBitmap(mBitmap);
                        break;
                    case 1:
                        mIzkcService.printImageGray(mBitmap);
                        break;
                    case 2:
                        mIzkcService.printRasterImage(mBitmap);
                        break;
                }
				if(autoOutputPaper){
					mIzkcService.generateSpace();
				}
			}
		} catch (RemoteException e) {
			Log.e("", "远程服务未连接...");
			e.printStackTrace();
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if(checkedId == R.id.rb_fontOne){
			fontType = 0;
		}else if(checkedId == R.id.rb_fontTwo){
			fontType = 1;
		}
	}
	
	private void wordToPic() {
		String str = et_printText.getText().toString();
		mBitmap = Bitmap.createBitmap(384, 30, Config.ARGB_8888);
		Canvas canvas = new Canvas(mBitmap);
		canvas.drawColor(Color.WHITE);
		TextPaint textPaint = new TextPaint();
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(25.0F);
		StaticLayout layout = new StaticLayout(str, textPaint,
				mBitmap.getWidth(), Alignment.ALIGN_NORMAL, (float) 1.0,
				(float) 0.0, true);
		layout.draw(canvas);
		iv_printPic.setImageBitmap(mBitmap);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()){
			case R.id.spinner_language:
				setPrinterLanguage(position);
				break;
			case R.id.spinner_pic_style:
			    imageType = position;
				break;
		}
	}

	private void setPrinterLanguage(int position) {
		LanguageModel map = (LanguageModel)spinnerLanguage.getItemAtPosition(position);
		String languageStr=map.language;
		//语言描述
		String description=map.description;
		//语言指令
		int code=map.code;
		Log.d(TAG, "onItemClick: spinner_language="+description+","+code);

		//发送语言切换指令
		byte[] cmd_language=new byte[]{0x1B,0x74,0x00};
		cmd_language[2]=(byte)code;
		try {
            mIzkcService.sendRAWData("print", cmd_language);
            //设置打印语言
        } catch (RemoteException e) {
            e.printStackTrace();
        }
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onDestroy() {
		unbindService();
		super.onDestroy();
	}


}
