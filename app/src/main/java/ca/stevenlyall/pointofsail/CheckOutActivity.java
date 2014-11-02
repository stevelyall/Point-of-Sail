package ca.stevenlyall.pointofsail;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class CheckOutActivity extends Activity {
	
	boolean saleCompleted;	
	private TextView subtotalTextView;
	private TextView taxTextView;
	private TextView totalTextView;
	private TextView amountTenderedTextView, amountTenderedLabel, changeAmountLabel, changeAmountTextView;
	private RadioGroup rg;
	private EditText amountEditText;
	
	private Button tenderButton, doneButton, newSaleButton;
	
	private double subtotal, tax, total, tendered;
	
	DecimalFormat df = new DecimalFormat("$0.00");
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_check_out);
	    
	    saleCompleted = false;
	    
	    subtotalTextView=(TextView)findViewById(R.id.subtotalTextView);
        taxTextView=(TextView)findViewById(R.id.taxTextView);
        totalTextView=(TextView)findViewById(R.id.totalTextView);
        amountTenderedTextView=(TextView)findViewById(R.id.amountTenderedTextView);
        changeAmountTextView=(TextView)findViewById(R.id.changeAmountTextView);
        amountTenderedLabel=(TextView)findViewById(R.id.amountTenderedLabel);
        changeAmountLabel=(TextView)findViewById(R.id.changeLabel);
        doneButton = (Button)findViewById(R.id.doneButton);
       
        newSaleButton = (Button)findViewById(R.id.newSaleButton);
        newSaleButton.setOnClickListener(new OnClickListener () {
        	public void onClick(View v) {
        		Intent newSale = new Intent(getBaseContext(),SaleActivity.class);
        		startActivity(newSale);
        		finish();
        	}
        });
        
		
        amountEditText = (EditText)findViewById(R.id.amountEditText);
        tenderButton = (Button)findViewById(R.id.tenderButton);
        tenderButton.setOnClickListener(new OnClickListener() {
        		@Override
				public void onClick(View v) {
					tenderSale();
				}     		
        });
        
        rg = (RadioGroup)findViewById(R.id.paymentMethods);
        rg.check(R.id.debitRadioButton);
        rg.requestFocus();
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
        		
        		if (group.getCheckedRadioButtonId() == R.id.cashRadioButton) {
        			amountEditText.setText("");
        		}
        		else {
        			amountEditText.setText(df.format(total));
        		}
        		
            }
		});
        
        getTotals();
        amountEditText.setText(df.format(total));

        Typeface pirate = Typeface.createFromAsset(getAssets(),"fonts/Treamd.ttf");
        doneButton.setTypeface(pirate);
        newSaleButton.setTypeface(pirate);
        tenderButton.setTypeface(pirate);
    }
	
	private void tenderSale() {
		boolean dontComplete = false;
		try {
			
			if (rg.getCheckedRadioButtonId() == 0) {
				AlertDialog noPaymentMethod = new AlertDialog.Builder(this).create();
				noPaymentMethod.setTitle("No payment method selected");
				noPaymentMethod.setMessage("You must choose a payment method.");
				noPaymentMethod.setButton(RESULT_OK, "Try again", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						hidePostSaleAmounts();						
					}
				});
				noPaymentMethod.show();
			}
			if (amountEditText.getText().toString().charAt(0)=='$') {
				tendered = Float.parseFloat(amountEditText.getText().toString().substring(1));
			}
			else {
				tendered = Float.parseFloat(amountEditText.getText().toString());
			}
			
			showPostSaleAmounts();
		}
		//amount paid not a double
		catch (NumberFormatException e) {
			dontComplete = true;
			AlertDialog invalidInput = new AlertDialog.Builder(this).create();
			invalidInput.setTitle("Avast ye scurvy dog!");
			invalidInput.setMessage("You must enter th' amount o' money yer customer has paid.");
			invalidInput.setButton(RESULT_OK, "Try again", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					hidePostSaleAmounts();
					return;
				}
			});
			invalidInput.show();
			
		}
		//amount paid left empty
		catch (StringIndexOutOfBoundsException ex) {
			dontComplete = true;
			AlertDialog invalidInput = new AlertDialog.Builder(this).create();
            invalidInput.setTitle("Avast ye scurvy dog!");
            invalidInput.setMessage("You must enter th' amount o' money yer customer has paid.");
			invalidInput.setButton(RESULT_OK, "Try again", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				    hidePostSaleAmounts();
					return;
				}
			});
			invalidInput.show();
			
		}
		
		if (dontComplete) {
			
			return;
		}
		
		
		// not enough paid
		if (!(total-tendered <= 0)) {
			AlertDialog notEnough = new AlertDialog.Builder(this).create();
			notEnough.setTitle("Shiver me timbers!");
			notEnough.setMessage("Ye must collect all yer plunder from yer customer.");
			notEnough.setButton(RESULT_OK, "Try again", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
								
				}
			});
			notEnough.show();
		}
		else {
			Toast.makeText(this, "Arr, well done, matey!", Toast.LENGTH_LONG).show();
			saleCompleted = true;
			doneButton.setVisibility(View.VISIBLE);
			tenderButton.setVisibility(View.INVISIBLE);
			doneButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent newSale = new Intent(getBaseContext(),SaleActivity.class);
	        		startActivity(newSale);
	        		finish();
					
				}
			});
		}
		

	}
	
	private void showPostSaleAmounts() {
		amountTenderedTextView.setText(df.format(tendered));
		amountTenderedLabel.setVisibility(View.VISIBLE);
		amountTenderedTextView.setVisibility(View.VISIBLE);
		changeAmountTextView.setText(df.format(total-tendered));
		changeAmountLabel.setVisibility(View.VISIBLE);
		changeAmountTextView.setVisibility(View.VISIBLE);
	}
	
	private void hidePostSaleAmounts() {
		amountTenderedLabel.setVisibility(View.INVISIBLE);
		amountTenderedTextView.setVisibility(View.INVISIBLE);
		changeAmountLabel.setVisibility(View.INVISIBLE);
		changeAmountTextView.setVisibility(View.INVISIBLE);
	}
	
	private void getTotals() {
		// load sale totals saved in shared prefs
		SharedPreferences totals = getSharedPreferences("totalAmounts", 0);
		subtotal = totals.getFloat("subtotal", 0.00f);
	    tax = totals.getFloat("tax", 0.00f);
	    total = totals.getFloat("total", 0.00f);
	    
	    // set textviews
		
	    subtotalTextView.setText(df.format(subtotal));
	    taxTextView.setText(df.format(tax));
	    totalTextView.setText(df.format(total));
	}

	
}
