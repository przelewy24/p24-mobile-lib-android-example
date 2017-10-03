package pl.przelewy24.p24example;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.UUID;

import pl.przelewy24.p24lib.settings.SettingsParams;
import pl.przelewy24.p24lib.transfer.TransferActivity;
import pl.przelewy24.p24lib.transfer.TransferResult;
import pl.przelewy24.p24lib.transfer.direct.TransactionParams;
import pl.przelewy24.p24lib.transfer.direct.TrnDirectParams;
import pl.przelewy24.p24lib.transfer.express.ExpressParams;
import pl.przelewy24.p24lib.transfer.passage.PassageCart;
import pl.przelewy24.p24lib.transfer.passage.PassageItem;
import pl.przelewy24.p24lib.transfer.request.TrnRequestParams;

public class P24ExampleActivity extends AppCompatActivity {

	private static final int TRANSFER_REQUEST_CODE = 28;
	private static final int TEST_MERCHANT_ID = 64195;
	private static final String TEST_CRC_SANDBOX = "d27e4cb580e9bbfe";
	private static final String TEST_CRC_SECURE = "b36147eeac447028";

	private Button btnBuy;
	private TextView txtStatus;

	private SwitchCompat switchSandbox;
	private RadioGroup radioGroup;
	private SettingsParams settingsParams;
	private EditText etToken;
	private EditText etUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		txtStatus = findViewById(R.id.txtStatus);
		txtStatus.setVisibility(View.GONE);

		btnBuy = findViewById(R.id.btnBuy);
		switchSandbox = findViewById(R.id.swithcSandbox);
		radioGroup = findViewById(R.id.radioGroup);
		etToken = findViewById(R.id.token);
		etUrl = findViewById(R.id.url);

		initSettingsParams();

		// start payment on button click
		btnBuy.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				txtStatus.setVisibility(View.GONE);

				switch (radioGroup.getCheckedRadioButtonId()) {
					case R.id.radioTransferTrnRequest:
						startTransferTrnRequest();
						break;
					case R.id.radioTransferTrnDirect:
						startTransferTrnDirect();
						break;
					case R.id.radioTransferExpress:
						startTransferExpress();
						break;
					case R.id.radioTransferPassage:
						startTransferPassage();
						break;
				}
			}
		});

		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup radioGroup, int i) {
				radioChanged(radioGroup.getCheckedRadioButtonId());
			}
		});
	}

	private void radioChanged(int checkedRadioButtonId) {
		hideTokenError();
		hideUrlError();
		switch (checkedRadioButtonId) {
			case R.id.radioTransferTrnRequest:
				showTokenInput();
				break;
			case R.id.radioTransferExpress:
				showUrlInput();
				break;
			default:
                showDirectInput();
		}
	}

    private void showDirectInput() {
        etUrl.setVisibility(View.GONE);
        etToken.setVisibility(View.GONE);
        switchSandbox.setVisibility(View.VISIBLE);
    }

	private void showTokenInput() {
		etToken.setVisibility(View.VISIBLE);
		etUrl.setVisibility(View.GONE);
		switchSandbox.setVisibility(View.VISIBLE);
	}

	private void showUrlInput() {
		etToken.setVisibility(View.GONE);
		etUrl.setVisibility(View.VISIBLE);
		switchSandbox.setVisibility(View.INVISIBLE);
	}

	private void initSettingsParams() {
		settingsParams = new SettingsParams();
		settingsParams.setSaveBankCredentials(true);
		settingsParams.setReadSmsPasswords(true);
	}

	private void startTransferTrnRequest() {
		if (TextUtils.isEmpty(etToken.getText())) {
			showTokenError();
		} else {
			hideTokenError();
			TrnRequestParams params = TrnRequestParams.create(etToken.getText().toString())
					.setSandbox(switchSandbox.isChecked())
					.setSettingsParams(settingsParams);

			Intent intent = TransferActivity.getIntentForTrnRequest(this, params);
			startActivityForResult(intent, TRANSFER_REQUEST_CODE);
		}
	}

	private void startTransferTrnDirect() {
		TrnDirectParams params = TrnDirectParams.create(getTestPayment())
				.setSandbox(switchSandbox.isChecked())
				.setSettingsParams(settingsParams);

		Intent intent = TransferActivity.getIntentForTrnDirect(this, params);
		startActivityForResult(intent, TRANSFER_REQUEST_CODE);
	}

	private void startTransferExpress() {
        if (TextUtils.isEmpty(etUrl.getText())) {
            showUrlError();
        } else {
            hideUrlError();
            ExpressParams params = ExpressParams.create(etUrl.getText().toString())
                    .setSettingsParams(settingsParams);

            Intent intent = TransferActivity.getIntentForExpress(this, params);
            startActivityForResult(intent, TRANSFER_REQUEST_CODE);
        }
	}

	private void startTransferPassage() {
		TrnDirectParams params = TrnDirectParams.create(getTestPaymentForPassage())
				.setSandbox(switchSandbox.isChecked())
				.setSettingsParams(settingsParams);

		Intent intent = TransferActivity.getIntentForTrnDirect(this, params);
		startActivityForResult(intent, TRANSFER_REQUEST_CODE);
	}

	public TransactionParams getTestPayment() {
        TransactionParams.Builder builder = getTestPaymentBuilder();
		return builder.build();
	}

	private TransactionParams.Builder getTestPaymentBuilder() {
		return new TransactionParams.Builder()
				.merchantId(TEST_MERCHANT_ID)
				.crc(getTestCrc())
				.sessionId(generateSessionId())
				.amount(1)
				.currency("PLN")
				.description("test payment desctiprion")
				.email("test@test.pl")
				.country("PL")
				.client("John Smith")
				.address("Test street")
				.zip("60-600")
				.city("Poznań")
				.phone("1246423234")
				.language("pl");

//                optional
//                .urlStatus(""https://url_status)
//                .method(25)
//                .timeLimit(90)
//                .channel(2)
//                .timeLimit(0)
//                .transferLabel("transfer label")
//                .shipping(0)
	}

	/**
	 * Generates random session id to be used in this example. In a real
	 * application in most cases session id should be generated by the server
	 * and retrieved in the application using some kind of web services.
	 *
	 * @return random session id
	 */
	private String generateSessionId() {
		return UUID.randomUUID().toString();
	}


	public String getTestCrc() {
		return switchSandbox.isChecked() ? TEST_CRC_SANDBOX : TEST_CRC_SECURE;
	}

	public TransactionParams getTestPaymentForPassage() {
        TransactionParams.Builder builder = getTestPaymentBuilder();
		PassageCart testPassageCart = getTestPassageCart();
		builder.passageCart(testPassageCart);
		return builder.build();
	}

	private PassageCart getTestPassageCart() {
		PassageCart passageCart = PassageCart.create();
		PassageItem.Builder builder;
		for (int i = 0; i < 10; i++) {
			int price = 2 * (100 + i);
			builder = new PassageItem.Builder()
					.name(String.format("Product name %d", i))
					.description(String.format("Product description %d", i))
					.number(i)
					.price(price / 2)
					.quantity(2)
					.targetAmount(price)
					.targetPosId(i / 2 == 1 ? 51986 : 51987);
			passageCart.addItem(builder.build());
		}

		return passageCart;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == TRANSFER_REQUEST_CODE) {

			// handle payment result
			if (resultCode == RESULT_OK) {
				TransferResult result = TransferActivity.parseResult(data);

				if (result.isSuccess()) {
					showSuccess("Transfer success");

				} else {
					showError("Transfer error. Code: " + result.getErrorCode());
				}
			} else {
				showCancel("Transfer canceled");
			}
		}
	}

	private void showMessage(String text, int red) {
		txtStatus.setText(text);
		txtStatus.setBackgroundColor(red);
		txtStatus.setVisibility(View.VISIBLE);
	}


	private void showSuccess(String text) {
		showMessage(text, Color.parseColor("#458B00"));
	}

	private void showError(String text) {
		showMessage(text, Color.RED);
	}

	private void showCancel(String text) {
		showMessage(text, Color.YELLOW);
	}


	private void hideTokenError() {
		etToken.setError(null);
	}

	private void showTokenError() {
		etToken.setError("You have to provide valid transaction token");
		etToken.requestFocus();
	}

	private void hideUrlError() {
		etToken.setError(null);
	}

	private void showUrlError() {
		etUrl.setError("You have to provide valid transaction url");
		etUrl.requestFocus();
	}

}

