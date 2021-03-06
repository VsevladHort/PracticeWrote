package com.dak.wrote.frontend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.filled.Create
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import compose.icons.FeatherIcons
import compose.icons.feathericons.Edit2

class TstVM : ViewModel() {
    val ms = mutableStateOf(0)
}


class TstVM2(he: Int) : ViewModel() {
    val ms = mutableStateOf(he)
}

class TstVM2CTOR(val i: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
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
//                controller.popBackStack() {
//
//                }
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

@Preview
@Composable
fun BottomNavTest() {
    var item by remember { mutableStateOf(0) }
    Scaffold(bottomBar = {
        BottomNavigation() {
            repeat(3) {
                BottomNavigationItem(selected = item == it, onClick = { item = it }, icon = {
                    Icon(
                        imageVector = FeatherIcons.Edit2,
                        contentDescription = "Edit $it"
                    )
                }, label = { Text(text = "Edit $it") })

            }
        }
    }) {
        Box(modifier = Modifier.padding(it))
        ViewModelTest {

        }
    }
}

@Preview
@Composable
fun SampleLayout(text: String = "uoua,uauauahutoaus oatu toau htoae uhoaten uhtoae utao tuaoeh") {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (title, viewAll) = createRefs()

        Text(text = "View all", Modifier
            .background(Color.Green)
            .constrainAs(viewAll) {
                top.linkTo(parent.top, 8.dp)
                end.linkTo(parent.end, 8.dp)
            })

        Text(text = "Short title uoa oau oe aoe ueo  uoa oeau oau e o ua u u oe eu oa",
//            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .background(Color.White)
                .constrainAs(title) {
                    top.linkTo(parent.top, 8.dp)
                    start.linkTo(parent.start, 8.dp)
                    end.linkTo(viewAll.start, 8.dp)
                    width = Dimension.fillToConstraints
                })
    }
}