package com.dak.wrote.frontend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Preview
@Composable
fun LazyTest() {
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            LazyColumn(
                Modifier.wrapTruly(), state = rememberLazyListState()
            ) {
                item { Text(text = "Hello") }
            }
        }
    }
}

fun Modifier.wrapTruly() = layout { measurable, constraints ->
    val placeable = measurable.measure(Constraints())
    layout(placeable.width, placeable.height) {
        placeable.placeRelative(0, 0)
    }
}

class TstVM : ViewModel() {
    val ms = mutableStateOf(0)
}


class TstVM2(he : Int) : ViewModel() {
    val ms = mutableStateOf(he)
}
class TstVM2CTOR(val i : Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TstVM2(i) as T
    }

}

@Composable
fun ViewModelTest(toOther: () -> Unit) {
    var vm = viewModel<TstVM2>(factory = TstVM2CTOR(0))
    var vm2 = viewModel<TstVM2>(factory = TstVM2CTOR(1))
    Column() {

        Text(text = "${vm == vm2}")
        TextButton(onClick = { vm.ms.value++ }) {
            Text(text = "Fst ${vm.ms.value}")
        }

        TextButton(onClick = { vm2.ms.value++ }) {
            Text(text = "Fst ${vm2.ms.value}")
        }

        Button(onClick = toOther) {
            Text(text = "Go to other")
        }
    }
}

@Preview
@Composable
fun NavigationTst() {
//    rememberNa
    var controller = rememberNavController()
    NavHost(navController = controller, startDestination = "1") {
        composable("1") {
            ViewModelTest {
                controller.navigate("2") {
                    popUpTo("2")
                }

            }
        }
        composable("2") {
            ViewModelTest {
//                controller.navigate("1") {
//                    popUpTo("1")
//                }
                controller.popBackStack()
            }
        }
    }
}


class HeheView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        ComposeView(requireContext()).apply {
            setContent {
                var vm = viewModel<TstVM>()
                TextButton(onClick = { vm.ms.value++ }) {
                    Text(text = "${vm.ms.value}")
                }
            }
        }

}
