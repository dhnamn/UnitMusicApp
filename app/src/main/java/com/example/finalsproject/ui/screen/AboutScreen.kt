package com.example.finalsproject.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.finalsproject.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = navBack) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronLeft,
                            contentDescription = stringResource(R.string.navigate_back)
                        )
                    }
                },
                title = {
                    Text(text = stringResource(R.string.about))
                },
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Member(
                name = "Nguyễn Quốc Trung",
                id = "21020946",
                roles = listOf(
                    "UI implementation",
                    "ViewModel design and implementation",
                    "Data level design and implementation"
                )
            )
            Member(
                name = "Nguyễn Hà Đức Thiện",
                id = "21020940",
                roles = listOf(
                    "Backend design and implementation"
                )
            )
            Member(
                name = "Đỗ Hoàng Nam",
                id = "21020930",
                roles = listOf(
                    "Backend implementation",
                    "Write unit test"
                )
            )
            Member(
                name = "Hàn Ngọc Minh",
                id = "21020507",
                roles = listOf(
                    "UI design and implementation",
                    "User test"
                )
            )
            Member(
                name = "Phạm Tường Minh",
                id = "21020145",
                roles = listOf(
                    "UI design and implementation",
                    "User test"
                )
            )
        }
    }
}

@Composable
private fun Member(
    name: String,
    id: String,
    roles: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "$name - $id",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        roles.forEach {
            Text(text = it, modifier = Modifier.padding(start = 16.dp))
        }
    }
}