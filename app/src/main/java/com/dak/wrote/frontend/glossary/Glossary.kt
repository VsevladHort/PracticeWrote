package com.dak.wrote.frontend.glossary

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dak.wrote.frontend.editor.AdditionalValuesView
import com.dak.wrote.frontend.viewmodel.GlossaryViewModel
import com.dak.wrote.frontend.viewmodel.GlossaryViewModelFactory

@Composable
fun GlossaryScreen(currentBookId: String, back: () -> Unit) {
    val viewModel = viewModel<GlossaryViewModel>(
        factory = GlossaryViewModelFactory(
            currentBookId,
            LocalContext.current.applicationContext as Application
        )
    )


}


@Composable
fun SuggestionSearch(
    textState: MutableState<String>,
    attributesState: SnapshotStateList<MutableState<String>>
) {

}

@Composable
fun SuggestionList(
    suggestions: MutableList<GlossaryViewModel.PartialNote>,
    onClick: (id: String) -> Unit
) {
    Column(Modifier.padding(10.dp), Arrangement.spacedBy(15.dp)) {
        suggestions.forEach { suggestion ->
            Surface(
                shape = RoundedCornerShape(30.dp),
                elevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(10.dp)) {

                    Text(
                        text = suggestion.title,
                        Modifier
                            .fillMaxWidth()
                            .clickable(
                                remember { MutableInteractionSource() },
                                rememberRipple(),
                                onClick = { onClick(suggestion.keyId) }
                            ),
                        style = MaterialTheme.typography.h5
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    AdditionalValuesView(
                        alternateNames = suggestion.alternateNames.toList(),
                        attributes = suggestion.attributes.map { at -> at.name }.toList()
                    )
                }
            }
        }
    }
}