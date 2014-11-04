// TODO banana can be added by ID but not anything else?

package ca.stevenlyall.pointofsail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SaleActivity extends Activity {

	// UI elements
	private ListView productsOnOrderListView;
	private ListView availProductListView;

	private TextView subtotalTextView;
	private TextView taxTextView;
	private TextView totalTextView;

	private EditText searchEditText;
	
	private Button checkOutButton, addButton, resetSaleButton, quitButton;

	ArrayAdapter<Product> selectedProdAdapter, availProdAdapter;
	
	ArrayList<Product> productsAvailable = new ArrayList<Product>();
	
	ArrayList<Product> productsSelected = new ArrayList<Product>();

	// Variables for sale
	final float TAX_RATE = 0.12f;
	private float total, tax, subtotal;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sale);
		
		loadProducts();
		//loadProductsNoFile();
		
		createAvailProductsList();
		productsOnOrderListView = (ListView) findViewById(R.id.productsOnTicket);
			
		subtotalTextView = (TextView) findViewById(R.id.subtotalTextView);
		taxTextView = (TextView) findViewById(R.id.taxTextView);
		totalTextView = (TextView) findViewById(R.id.totalTextView);
		searchEditText = (EditText) findViewById(R.id.searchEditText);
		availProductListView.requestFocus();

		checkOutButton = (Button) findViewById(R.id.doneButton);
		addButton = (Button) findViewById(R.id.addButton);
        quitButton = (Button) findViewById(R.id.quitButton);
        resetSaleButton = (Button) findViewById(R.id.resetSale);

        Typeface pirate = Typeface.createFromAsset(getAssets(),"fonts/Treamd.ttf");
        quitButton.setTypeface(pirate);
        checkOutButton.setTypeface(pirate);
        addButton.setTypeface(pirate);
        resetSaleButton.setTypeface(pirate);

		selectedProdAdapter = new ProductsOnOrderAdapter();
		productsOnOrderListView.setAdapter(selectedProdAdapter);
		productsOnOrderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Product productClicked = productsSelected.get(position);
				removeProduct(productClicked);			
			}
		});

       quitButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View V) {

               finish();
           }
       });

        // reset button clears all
        View.OnClickListener resetListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Resetting Sale...", Toast.LENGTH_SHORT).show();
                for (Product p : productsSelected) {
                    p.setQuantity(1);
                }
                productsSelected.clear();
                selectedProdAdapter.notifyDataSetChanged();
                updateTotals();
                Log.i("buttonClick", "Reset button clicked, sale reset");
            }
        };
        resetSaleButton.setOnClickListener(resetListener);

        // handler for check out button click, starts next activity and saves totals to shared prefs
        View.OnClickListener checkOutListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog noItems = new AlertDialog.Builder((SaleActivity.this)).create();
                noItems.setTitle("Stand and deliver!");
                noItems.setMessage("Ye must add some items to th' sale before checkin' out!");
                noItems.setButton(RESULT_OK, "Try again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                if (productsSelected.isEmpty()) {
                    noItems.show();
                }
                else {
                    checkOutSale();
                }
            }
        };
        checkOutButton.setOnClickListener(checkOutListener);

       addButton.setOnClickListener( new View.OnClickListener() {
           @Override
           // TODO addButton works for first item but thats it
           // Handler for Add button. If user has typed a valid product ID in search field, adds the product to the order, shows a toast otherwise
           public void onClick(View v) {
               int productToFind;
               try {
                   productToFind = Integer.parseInt(searchEditText.getText().toString());
                   for (Product p : productsAvailable) {
                       if (p.getProductNum() == productToFind) {
                           addProduct(p);
                           searchEditText.setText("");
                           return;
                       } else {
                           Toast.makeText(getBaseContext(), "Product " + productToFind + " not found.", Toast.LENGTH_SHORT).show();
                           searchEditText.setText("");
                       }
                   }

               } catch (NumberFormatException e) {
                   Toast.makeText(getBaseContext(), "You must enter a product ID number.", Toast.LENGTH_SHORT).show();
                   searchEditText.setText("");
               }
           }

       });
	}

	// loads the product list from the text file, adds values to ArrayList. Works fine, but not used right now
	public void loadProducts() {
		try {
            AssetManager am = getAssets();
            InputStream stream = am.open("products.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));


			String line = "";
			while ((line = br.readLine())!=null) {
                Log.i("ProductFile", "current line is " + line);
				int a = line.indexOf(',');
				int b = line.lastIndexOf(',');
	
				int num = 0;
				String name = "";
				double price = 0.0;
	
				try {
					num = Integer.parseInt(line.substring(0, a));
					name = line.substring(a + 1, b);
					price = Double.parseDouble(line.substring(b + 1,
							line.length()));
				} catch (NumberFormatException nfe) {
					Log.e("ProductFile", "Problem parsing product data", nfe);
					nfe.printStackTrace();
				}
				Log.i("ProductFile", "Product data file item load success");
				productsAvailable.add(new Product(num, name, price));
			}
			br.close();

		} catch (IOException e) {
			Log.e("ProductFile", "Problem reading products.txt", e);
			e.printStackTrace();
		}
	}
	
	// populates the available products listview with some basic test items without reading from file, for testing
	public void loadProductsNoFile() {
		productsAvailable.add(new Product(8534,"Rum",9.99));
		productsAvailable.add(new Product(6011,"Spyglass",6.99));
		productsAvailable.add(new Product(7218,"Pistol",14.98));
		productsAvailable.add(new Product(1287,"Rapier",4.99));
		productsAvailable.add(new Product(8531,"Wench",18.97));
		productsAvailable.add(new Product(7108,"Parrot",3.98));
		productsAvailable.add(new Product(2943,"Pegleg",3.49));
        productsAvailable.add(new Product(3257,"Hook",2.98));

	}
	
	// set up the listView, Adapter and listener for the list of available products
	public void createAvailProductsList() {
		availProductListView = (ListView) findViewById(R.id.availableProducts);
        availProdAdapter = new ProductListAdapter();
		
		availProductListView.setAdapter(availProdAdapter);
		availProductListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View clickedItem,
					int position, long id) {
				Product productClicked = productsAvailable.get(position);
				addProduct(productClicked);
			}
		});
	}
	
	// custom adapter for custom list view
	private class ProductListAdapter extends ArrayAdapter<Product> {
		public ProductListAdapter() {
			super(getBaseContext(), R.layout.product_list_item, productsAvailable);
		}
		
		@Override
		public View getView(int index, View v, ViewGroup parent) {
			DecimalFormat df = new DecimalFormat("$0.00");
			//view for listview	
			View productView = v;
			if (productView == null) {
				// use layout for each list item
				productView = getLayoutInflater().inflate(R.layout.product_list_item, parent, false);
			}
			
			// get each product and fill listview TextViews
			Product currProduct = productsAvailable.get(index);
			
			TextView productID = (TextView)productView.findViewById(R.id.product_ID);
			productID.setText(Integer.toString(currProduct.getProductNum()));
			
			TextView productName = (TextView)productView.findViewById(R.id.product_name);
			productName.setText(currProduct.getName());
			
			TextView productQty = (TextView)productView.findViewById(R.id.product_quantity);
			productQty.setText("");
			
			TextView productPrice =(TextView)productView.findViewById(R.id.product_price);
			productPrice.setText(df.format(currProduct.getPrice()));
			
			return productView;
		}
	}
	
	// same as above, but for the products on the order
	private class ProductsOnOrderAdapter extends ArrayAdapter<Product> {
		public ProductsOnOrderAdapter() {
			super(getBaseContext(), R.layout.product_list_item, productsSelected);
		}
		
		@Override
		public View getView(int index, View v, ViewGroup parent) {
			DecimalFormat df = new DecimalFormat("$0.00");
			View productView = v;
			if (productView == null) {
				productView = getLayoutInflater().inflate(R.layout.product_list_item, parent, false);
			}
			
			Product currProduct = productsSelected.get(index);
			
			TextView productID = (TextView)productView.findViewById(R.id.product_ID);
			productID.setText(Integer.toString(currProduct.getProductNum()));
			
			TextView productName = (TextView)productView.findViewById(R.id.product_name);
			productName.setText(currProduct.getName());
			
			TextView productQty = (TextView)productView.findViewById(R.id.product_quantity);
			productQty.setText(Integer.toString(currProduct.getQuantity()));
			
			TextView productPrice =(TextView)productView.findViewById(R.id.product_price);
			productPrice.setText(df.format(currProduct.getPrice()));
			
			return productView;
		}
	}

	

	
	// saves totals in shared prefs, starts new activity
	private void checkOutSale() {
		//Toast.makeText(getBaseContext(), "Checking out...", Toast.LENGTH_LONG).show();
		Log.i("buttonClick","checkOut button clicked, starting check out activity");
		
		SharedPreferences totals = getSharedPreferences("totalAmounts", 0);
		SharedPreferences.Editor edit = totals.edit();
		edit.putFloat("subtotal", subtotal);
		edit.putFloat("tax", tax);
		edit.putFloat("total", total);
		edit.commit();
		
		Intent checkOutSale = new Intent(this, CheckOutActivity.class);
		
		startActivity(checkOutSale);
        finish();
	}
	


	// updates totals for products in selected products arraylist
	private void updateTotals() {
		DecimalFormat df = new DecimalFormat("$0.00");
		subtotal = 0;
		for (Product p : productsSelected) {
			subtotal += (p.getPrice() * p.getQuantity());
			Log.i("total", "adding " + p.getPrice() + "to subtotal");
		}
		Log.i("total", "subtotal after update is " + subtotal);

		tax = subtotal * TAX_RATE;
		total = subtotal + tax;

		subtotalTextView.setText((df.format(subtotal)));
		taxTextView.setText(df.format(tax));
		totalTextView.setText(df.format(total));
	}
	
	private void removeProduct(Product productClicked) {
		if (productClicked.getQuantity()>1) {
            productClicked.setQuantity(productClicked.getQuantity()-1);
        }
        else {
            productsSelected.remove(productClicked);
        }
            selectedProdAdapter.notifyDataSetChanged();
		updateTotals();
		
	}

	private void addProduct(Product toAdd) {
		
		// check whether clicked product is already on order
		for(Product p : productsSelected) {
			Log.i("products",p.toString());
		} 
		boolean prodOnOrder = false;
		for (Product p : productsSelected) {
			if (p.equals(toAdd)) {
				Log.i("product", "p equals to add");
				prodOnOrder = true;
				p.setQuantity(p.getQuantity()+1); // update quantity
			}
		}
		
		if (!prodOnOrder) { //add new product to order
			productsSelected.add(toAdd);
			Log.i("products","Adding product: " + toAdd.toString());
		}
		Log.i("products", "printing products");
		for(Product p : productsSelected) {
			Log.i("products",p.toString());
		}
		selectedProdAdapter.notifyDataSetChanged();
		updateTotals();
	}
	
}
