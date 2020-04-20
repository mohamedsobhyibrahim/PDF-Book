package com.mohaedsobhy.pdfbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.mohaedsobhy.pdfbook.service.PageManager;

public class MainActivity extends AppCompatActivity {

    private PDFView pdfView;
    private PageManager pageManager;
    private Toolbar toolbar;
    private EditText toolbarSearchEditText;

    private ImageView bookmarkImageView;
    private TextView bookmarkTextView;

    private String lastPageNumber;
    private final static String BOOK_URI = "book.pdf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initToolbar();
        initSearchEditText();

        lastPageNumber = pageManager.getPage();
        initPDF(Integer.parseInt(lastPageNumber));

        setBookmark();
    }

    private void setBookmark() {
        bookmarkImageView.setVisibility(View.VISIBLE);
        bookmarkTextView.setVisibility(View.VISIBLE);
        bookmarkTextView.setText(lastPageNumber);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bookmarkImageView.setVisibility(View.GONE);
                bookmarkTextView.setVisibility(View.GONE);
            }
        }, 3000);
    }

    private void initUI() {
        pdfView = findViewById(R.id.pdfView);
        bookmarkImageView = findViewById(R.id.imageView_bookmark);
        bookmarkTextView = findViewById(R.id.textView_page);

        pdfView = findViewById(R.id.pdfView);
        pageManager = new PageManager(this);
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void initPDF(int defaultPageNumber) {
        pdfView.fromAsset(BOOK_URI)
                .enableSwipe(true)
                .enableDoubletap(true)
                .swipeVertical(true)
                .defaultPage(defaultPageNumber)
                .showMinimap(false)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        lastPageNumber = String.valueOf(page);
                        String pageStr = lastPageNumber + "";
                        toolbarSearchEditText.setText(pageStr);
                    }
                })
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        showToast(getString(R.string.error_message));
                    }
                })
                .enableAnnotationRendering(false)
                .password(null)
                .showPageWithAnimation(true)
                .load();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pageManager.storePage(lastPageNumber);
    }

    private void initSearchEditText() {
        toolbarSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String page = toolbarSearchEditText.getText().toString().trim();
                    if (!page.isEmpty()
                            && Integer.parseInt(page) > 0
                            && Integer.parseInt(page) <= 274) {
                        initPDF(Integer.parseInt(page));
                        toolbarSearchEditText.setText("");
                    } else {
                        showToast(getString(R.string.page_error));
                    }

                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbarSearchEditText = findViewById(R.id.toolbar_search_editText);
        setSupportActionBar(toolbar);
    }

    private void hideSoftKeyboard() {
        View view = MainActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null && imm.isAcceptingText()) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
