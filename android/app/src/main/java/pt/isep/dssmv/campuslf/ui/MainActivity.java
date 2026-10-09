package com.campuslf.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import com.campuslf.app.R;
import com.campuslf.app.model.FoundItem;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Ecrã principal da aplicação. */
public final class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView foundItemsList = findViewById(R.id.foundItemsList);
        foundItemsList.setEmptyView(findViewById(R.id.emptyListText));

        // Dados temporários apenas para testar a apresentação da lista.
        List<FoundItem> sampleItems = new ArrayList<>();
        sampleItems.add(new FoundItem(
                1,
                "Keys with blue keyring",
                "Three keys found near a study table.",
                "Keys",
                "Library",
                LocalDate.now()));
        sampleItems.add(new FoundItem(
                2,
                "Black backpack",
                "Small black backpack.",
                "Other",
                "Building B",
                LocalDate.now().minusDays(1)));

        foundItemsList.setAdapter(new FoundItemAdapter(this, sampleItems));
    }
}
