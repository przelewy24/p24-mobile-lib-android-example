package pl.przelewy24.p24example;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import java.util.UUID;

import pl.przelewy24.p24lib.card.CardData;
import pl.przelewy24.p24lib.card.RegisterCardActivity;
import pl.przelewy24.p24lib.card.RegisterCardParams;
import pl.przelewy24.p24lib.card.RegisterCardResult;
import pl.przelewy24.p24lib.google_pay.GooglePayActivity;
import pl.przelewy24.p24lib.google_pay.GooglePayParams;
import pl.przelewy24.p24lib.google_pay.GooglePayResult;
import pl.przelewy24.p24lib.google_pay.GooglePayTransactionRegistrar;
import pl.przelewy24.p24lib.settings.SdkConfig;
import pl.przelewy24.p24lib.transfer.TransferActivity;
import pl.przelewy24.p24lib.transfer.TransferResult;
import pl.przelewy24.p24lib.transfer.direct.TransactionParams;
import pl.przelewy24.p24lib.transfer.direct.TrnDirectParams;
import pl.przelewy24.p24lib.transfer.express.ExpressParams;
import pl.przelewy24.p24lib.transfer.passage.PassageCart;
import pl.przelewy24.p24lib.transfer.passage.PassageItem;
import pl.przelewy24.p24lib.transfer.request.TrnRequestParams;

public class P24ExampleActivity extends AppCompatActivity {

    private static final int CARD_REGISTER_REQUEST_CODE = 26;
    private static final int TRANSFER_REQUEST_CODE = 28;
    private static final int GOOGLE_PAY_REQUEST_CODE = 29;
    private static final int TEST_MERCHANT_ID = 64195;
    private static final String TEST_CRC_SANDBOX = "d27e4cb580e9bbfe";
    private static final String TEST_CRC_SECURE = "b36147eeac447028";

    private Button btnBuy;
    private TextView txtStatus;
    private MaterialCardView statusCard;

    private SwitchCompat switchSandbox;
    private RadioGroup radioGroup;
    private EditText etToken;
    private EditText etUrl;
    private TextInputLayout tokenInputLayout;
    private TextInputLayout urlInputLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this, SystemBarStyle.light(Color.WHITE, Color.WHITE));
        setContentView(R.layout.main);

        txtStatus = findViewById(R.id.txtStatus);
        statusCard = findViewById(R.id.statusCard);

        btnBuy = findViewById(R.id.btnBuy);
        switchSandbox = findViewById(R.id.switchSandbox);
        radioGroup = findViewById(R.id.radioGroup);
        etToken = findViewById(R.id.token);
        etUrl = findViewById(R.id.url);
        tokenInputLayout = findViewById(R.id.tokenInputLayout);
        urlInputLayout = findViewById(R.id.urlInputLayout);

        radioChanged(radioGroup.getCheckedRadioButtonId());

        btnBuy.setOnClickListener(arg0 -> {
            statusCard.setVisibility(View.GONE);

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
                case R.id.radioGooglePay:
                    startGooglePay();
                    break;
                case R.id.radioRegisterCard:
                    startCardRegister();
                    break;
            }
        });

        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> radioChanged(radioGroup.getCheckedRadioButtonId()));

        SdkConfig.setCertificatePinningEnabled(false);
    }

    private void radioChanged(int checkedRadioButtonId) {
        hideTokenError();
        hideUrlError();
        switch (checkedRadioButtonId) {
            case R.id.radioTransferTrnRequest:
                showTokenInput();
                break;
            case R.id.radioTransferExpress:
                etUrl.setText("https://e.przelewy24.pl/ESGByXP2S1SMxDm");
                showUrlInput();
                break;
            case R.id.radioRegisterCard:
                etUrl.setText("https://sandbox.przelewy24.pl/bundle/card?lang=PL&merchantId=46862&userId=y8vp5sf5wf&sessionId=1&sign=ce91e29bfdf708c2989f610cc955b5dc4b3fdb5619762d4332550c37d6e6a7b5f049ca3c9ad1e89977a7e82287bacdef");
                showUrlInput();
                break;
            default:
                showDirectInput();
        }
    }

    private void showDirectInput() {
        etUrl.setVisibility(View.GONE);
        etToken.setVisibility(View.GONE);
        tokenInputLayout.setVisibility(View.GONE);
        urlInputLayout.setVisibility(View.GONE);
        switchSandbox.setVisibility(View.VISIBLE);
    }

    private void showTokenInput() {
        etToken.setVisibility(View.VISIBLE);
        etUrl.setVisibility(View.GONE);
        tokenInputLayout.setVisibility(View.VISIBLE);
        urlInputLayout.setVisibility(View.GONE);
        switchSandbox.setVisibility(View.VISIBLE);
    }

    private void showUrlInput() {
        etToken.setVisibility(View.GONE);
        etUrl.setVisibility(View.VISIBLE);
        tokenInputLayout.setVisibility(View.GONE);
        urlInputLayout.setVisibility(View.VISIBLE);
        switchSandbox.setVisibility(View.INVISIBLE);
    }

    private void startTransferTrnRequest() {
        if (TextUtils.isEmpty(etToken.getText())) {
            showTokenError();
        } else {
            hideTokenError();
            TrnRequestParams params = TrnRequestParams.create(etToken.getText().toString())
                    .setSandbox(switchSandbox.isChecked());

            Intent intent = TransferActivity.getIntentForTrnRequest(this, params);
            startActivityForResult(intent, TRANSFER_REQUEST_CODE);
        }
    }

    private void startTransferTrnDirect() {
        TrnDirectParams params = TrnDirectParams.create(getTestPayment())
                .setSandbox(switchSandbox.isChecked());

        Intent intent = TransferActivity.getIntentForTrnDirect(this, params);
        startActivityForResult(intent, TRANSFER_REQUEST_CODE);
    }

    private void startTransferExpress() {
        if (TextUtils.isEmpty(etUrl.getText())) {
            showUrlError();
        } else {
            hideUrlError();
            ExpressParams params = ExpressParams.create(etUrl.getText().toString());

            Intent intent = TransferActivity.getIntentForExpress(this, params);
            startActivityForResult(intent, TRANSFER_REQUEST_CODE);
        }
    }

    private void startTransferPassage() {
        TrnDirectParams params = TrnDirectParams.create(getTestPaymentForPassage())
                .setSandbox(switchSandbox.isChecked());

        Intent intent = TransferActivity.getIntentForTrnDirect(this, params);
        startActivityForResult(intent, TRANSFER_REQUEST_CODE);
    }

    private void startGooglePay() {
        GooglePayParams params = GooglePayParams.create(TEST_MERCHANT_ID, 1, "PLN")
                .setSandbox(switchSandbox.isChecked());

        Intent intent = GooglePayActivity.getStartIntent(this, params, getGooglePayTrnRegistrar());
        startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);
    }

    private GooglePayTransactionRegistrar getGooglePayTrnRegistrar() {
        return (methodRefId, callback) -> callback.onTransactionRegistered("D3B5D31A55-0DD4D9-D7CA8F-FC02ECD7F0");
    }

    private void startCardRegister() {
        if (TextUtils.isEmpty(etUrl.getText())) {
            showUrlError();
        } else {
            hideUrlError();
            CardData cardData = new CardData(
                    "1111111111111111",
                    4, 2021,
                    "452"
            );

//            RegisterCardParams params = RegisterCardParams.create(etUrl.getText().toString());
            RegisterCardParams params = RegisterCardParams.createPrefilled(etUrl.getText().toString(), cardData);
            Intent pIntent = RegisterCardActivity.getStartIntent(getApplicationContext(), params);
            startActivityForResult(pIntent, CARD_REGISTER_REQUEST_CODE);
        }
    }


    private TransactionParams getTestPayment() {
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
                .description("test payment description")
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

        // handle payment result
        if (requestCode == TRANSFER_REQUEST_CODE) {
            onTransferResult(resultCode, data);
        } else if (requestCode == GOOGLE_PAY_REQUEST_CODE) {
            onGooglePayResult(resultCode, data);
        } else if (requestCode == CARD_REGISTER_REQUEST_CODE) {
            onCardRegisterResult(resultCode, data);
        }
    }

    private void onTransferResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            TransferResult result = TransferActivity.parseResult(data);

            if (result.isSuccess()) {
                showSuccess("Transfer success");

            } else {
                showError("Transfer error. Code: " + result.getErrorCode());
            }
        }
        else {
            showCancel("Transfer canceled");
        }
    }

    private void onGooglePayResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            GooglePayResult result = GooglePayActivity.parseResult(data);
            if (result.isError())
                showError("Google Pay error. Code: " + result.getErrorCode());

            if (result.isCompleted())
                showSuccess("Google Pay completed");
        } else {
            showCancel("Google Pay canceled");
        }
    }

    private void onCardRegisterResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            RegisterCardResult cardRegisterResult = RegisterCardActivity.parseResult(data);

            if (cardRegisterResult.isError()) {
                showError("Wystąpił błąd: Code: " + cardRegisterResult.getErrorCode());
            } else {
                showSuccess("karta zarejestrowana, token:" + cardRegisterResult.getCardToken());
            }
        }
        else {
            showCancel("Rejestracja karty anulowana");
        }
    }

    private void showMessage(String text, int backgroundColor, int textColor) {
        txtStatus.setText(text);
        txtStatus.setTextColor(textColor);
        statusCard.setCardBackgroundColor(backgroundColor);
        statusCard.setVisibility(View.VISIBLE);
    }

    private void showSuccess(String text) {
        showMessage(text, ContextCompat.getColor(this, R.color.color_success), Color.WHITE);
    }

    private void showError(String text) {
        showMessage(text, ContextCompat.getColor(this, R.color.color_error), Color.WHITE);
    }

    private void showCancel(String text) {
        showMessage(text, ContextCompat.getColor(this, R.color.color_cancel), Color.WHITE);
    }

    private void hideTokenError() {
        etToken.setError(null);
    }

    private void showTokenError() {
        etToken.setError(getString(R.string.error_token_required));
        etToken.requestFocus();
    }

    private void hideUrlError() {
        etUrl.setError(null);
    }

    private void showUrlError() {
        etUrl.setError(getString(R.string.error_url_required));
        etUrl.requestFocus();
    }
}