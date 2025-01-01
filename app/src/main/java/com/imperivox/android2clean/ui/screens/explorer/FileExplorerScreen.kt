package com.imperivox.android2clean.ui.screens.explorer

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.imperivox.android2clean.ui.components.explorer.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun FileExplorerScreen(
    onNavigateBack: () -> Unit,
    viewModel: FileExplorerViewModel = viewModel()
) {
    val currentPath by viewModel.currentPath.collectAsState()
    val files by viewModel.files.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val hasPermission = remember { mutableStateOf(false) }

    val fileToRename by viewModel.showRenameDialog.collectAsState()
    val fileToCopy by viewModel.showCopyDialog.collectAsState()
    val fileToMove by viewModel.showMoveDialog.collectAsState()

    val permissions = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        }
        else -> listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    val permissionsState = rememberMultiplePermissionsState(permissions) { granted ->
        hasPermission.value = granted.all { it.value }
    }

    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        } else {
            hasPermission.value = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("File Explorer") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.navigateUp() }) {
                        Icon(Icons.Default.ArrowUpward, "Up")
                    }
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        if (!hasPermission.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Storage permission is required")
                    Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                        Text("Grant Permission")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                SearchBar(
                    onSearch = { query, searchContent ->
                        viewModel.searchFiles(query, searchContent)
                    },
                    modifier = Modifier.padding(16.dp)
                )

                Text(
                    text = currentPath,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                if (isSearching) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    FileList(
                        files = files,
                        onFileClick = { file ->
                            if (file.isDirectory) {
                                viewModel.navigateTo(file.path)
                            }
                        },
                        onFileAction = { file, action ->
                            when (action) {
                                FileAction.Copy -> viewModel.initiateCopy(file)
                                FileAction.Move -> viewModel.initiateMove(file)
                                FileAction.Delete -> viewModel.deleteFile(file)
                                FileAction.Rename -> viewModel.initiateRename(file)
                            }
                        }
                    )
                }
            }
        }
    }

    // File operation dialogs
    fileToRename?.let { file ->
        RenameDialog(
            currentName = file.name,
            onDismiss = { viewModel.cancelFileOperation() },
            onConfirm = { newName -> viewModel.performRename(file, newName) }
        )
    }

    fileToCopy?.let { file ->
        FileOperationDialog(
            title = "Copy File",
            currentPath = file.path,
            onDismiss = { viewModel.cancelFileOperation() },
            onConfirm = { targetPath -> viewModel.performCopy(file, targetPath) }
        )
    }

    fileToMove?.let { file ->
        FileOperationDialog(
            title = "Move File",
            currentPath = file.path,
            onDismiss = { viewModel.cancelFileOperation() },
            onConfirm = { targetPath -> viewModel.performMove(file, targetPath) }
        )
    }
}