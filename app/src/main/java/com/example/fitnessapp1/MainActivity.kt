package com.example.fitnessapp1   // важно: совпадает с путём в проекте

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessapp1.ui.theme.FitnessAPP1Theme
import androidx.compose.material3.ExperimentalMaterial3Api

data class Program(val id: String, val name: String)

data class Exercise(
    val id: Long,
    val programId: String,
    val title: String,
    val sets: Int,
    val reps: Int
)

sealed class Screen {
    object ProgramList : Screen()
    data class ProgramDetail(val programId: String) : Screen()
    data class EditExercise(val programId: String, val exerciseId: Long? = null) : Screen()
}

// ----- Activity -----

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Тема теперь контролируется изнутри приложения
            // darkMode состояние хранится в FitnessAppRoot
            FitnessAppRootHolder()
        }
    }
}


@Composable
fun FitnessAppRootHolder() {
    var darkMode by remember { mutableStateOf(false) } // начальная — светлая (можно заменить на isSystemInDarkTheme())
    FitnessAPP1Theme(darkTheme = darkMode) {
        Surface(modifier = Modifier.fillMaxSize()) {
            FitnessAppRoot(darkMode = darkMode, onToggleDark = { darkMode = !darkMode })
        }
    }
}



@Composable
fun FitnessAppRoot(darkMode: Boolean, onToggleDark: () -> Unit) {

    val programsState = remember {
        mutableStateListOf(
            Program("chest", "Chest day"),
            Program("legs", "Leg day"),
            Program("shoulders", "Shoulder day")
        )
    }

    var nextProgramSuffix by remember { mutableStateOf(1) }

    var screen by remember { mutableStateOf<Screen>(Screen.ProgramList) }

    // список упражнений в памяти
    var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    var nextId by remember { mutableStateOf(1L) }

    fun addProgram(name: String): String? {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return "Name required"
        // уникальный id
        val id = "p${System.currentTimeMillis()}${nextProgramSuffix++}"
        programsState.add(Program(id, trimmed))
        return null
    }

    fun editProgram(id: String, newName: String): String? {
        val trimmed = newName.trim()
        if (trimmed.isBlank()) return "Name required"
        val idx = programsState.indexOfFirst { it.id == id }
        if (idx != -1) {
            programsState[idx] = programsState[idx].copy(name = trimmed)
        }
        return null
    }

    fun deleteProgram(id: String) {
        programsState.removeAll { it.id == id }
        exercises = exercises.filterNot { it.programId == id }
    }

    fun addExercise(programId: String, title: String, sets: Int, reps: Int): String? {
        if (title.isBlank()) return "Title is required"
        if (sets <= 0 || reps <= 0) return "Sets/Reps must be > 0"
        val e = Exercise(nextId++, programId, title.trim(), sets, reps)
        exercises = exercises + e
        return null
    }

    fun updateExercise(id: Long, programId: String, title: String, sets: Int, reps: Int): String? {
        if (title.isBlank()) return "Title is required"
        if (sets <= 0 || reps <= 0) return "Sets/Reps must be > 0"
        exercises = exercises.map {
            if (it.id == id) it.copy(programId = programId, title = title.trim(), sets = sets, reps = reps)
            else it
        }
        return null
    }

    fun deleteExercise(id: Long) {
        exercises = exercises.filterNot { it.id == id }
    }

    when (val s = screen) {
        is Screen.ProgramList -> ProgramListScreen(
            programs = programsState,
            onOpen = { pid -> screen = Screen.ProgramDetail(pid) },
            onAddProgram = { /* handled by dialog inside composable */ },
            onEditProgram = { /* handled by dialog inside composable */ },
            onDeleteProgram = { id -> deleteProgram(id) },
            addProgramAction = { name -> addProgram(name) },
            editProgramAction = { id, name -> editProgram(id, name) },
            darkMode = darkMode,
            onToggleDark = onToggleDark
        )

        is Screen.ProgramDetail -> ProgramDetailScreen(
            programs = programsState,
            exercises = exercises.filter { it.programId == s.programId },
            programId = s.programId,
            onBack = { screen = Screen.ProgramList },
            onAdd = { screen = Screen.EditExercise(s.programId, null) },
            onEdit = { exId -> screen = Screen.EditExercise(s.programId, exId) },
            onDelete = { id -> deleteExercise(id) },
            darkMode = darkMode,
            onToggleDark = onToggleDark
        )

        is Screen.EditExercise -> EditExerciseScreen(
            programId = s.programId,
            existing = exercises.firstOrNull { it.id == s.exerciseId },
            onSave = { title, sets, reps, idOpt ->
                val err = if (idOpt == null)
                    addExercise(s.programId, title, sets, reps)
                else
                    updateExercise(idOpt, s.programId, title, sets, reps)
                if (err == null) screen = Screen.ProgramDetail(s.programId)
                err
            },
            onCancel = { screen = Screen.ProgramDetail(s.programId) },
            darkMode = darkMode,
            onToggleDark = onToggleDark
        )
    }
}

// ----- screens -----


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramListScreen(
    programs: List<Program>,
    onOpen: (String) -> Unit,
    onAddProgram: () -> Unit,
    onEditProgram: (String) -> Unit,
    onDeleteProgram: (String) -> Unit,
    addProgramAction: (String) -> String?,
    editProgramAction: (String, String) -> String?,
    darkMode: Boolean,
    onToggleDark: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Pair<Boolean, String?>>(false to null) } // Pair(show, id)
    var inputText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose workout day") },
                actions = {
                    // переключатель тёмной темы в AppBar
                    Row(Modifier.padding(end = 8.dp)) {
                        Text(if (darkMode) "Dark" else "Light", modifier = Modifier.padding(end = 8.dp))
                        Switch(checked = darkMode, onCheckedChange = { onToggleDark() })
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) { Text("+") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            items(programs.size) { i ->
                val p = programs[i]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column(modifier = Modifier
                            .weight(1f)
                            .clickable { onOpen(p.id) }) {
                            Text(p.name, style = MaterialTheme.typography.titleLarge)
                            Text("Tap to view exercises")
                        }
                        Row {
                            TextButton(onClick = {
                                inputText = p.name
                                errorText = null
                                showEditDialog = true to p.id
                            }) { Text("Edit") }
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(onClick = { onDeleteProgram(p.id) }) { Text("Delete") }
                        }
                    }
                }
            }
        }
    }

    // Add dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false; inputText = ""; errorText = null },
            confirmButton = {
                TextButton(onClick = {
                    val err = addProgramAction(inputText)
                    if (err == null) {
                        showAddDialog = false
                        inputText = ""
                        errorText = null
                    } else {
                        errorText = err
                    }
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false; inputText = ""; errorText = null }) { Text("Cancel") }
            },
            title = { Text("Add workout day") },
            text = {
                Column {
                    OutlinedTextField(value = inputText, onValueChange = { inputText = it }, label = { Text("Day name") }, singleLine = true)
                    errorText?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            }
        )
    }

    // Edit dialog
    if (showEditDialog.first) {
        val editId = showEditDialog.second ?: ""
        AlertDialog(
            onDismissRequest = { showEditDialog = false to null; inputText = ""; errorText = null },
            confirmButton = {
                TextButton(onClick = {
                    val err = editProgramAction(editId, inputText)
                    if (err == null) {
                        showEditDialog = false to null
                        inputText = ""
                        errorText = null
                    } else {
                        errorText = err
                    }
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false to null; inputText = ""; errorText = null }) { Text("Cancel") }
            },
            title = { Text("Edit workout day") },
            text = {
                Column {
                    OutlinedTextField(value = inputText, onValueChange = { inputText = it }, label = { Text("Day name") }, singleLine = true)
                    errorText?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramDetailScreen(
    programs: List<Program>,
    exercises: List<Exercise>,
    programId: String,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    darkMode: Boolean,
    onToggleDark: () -> Unit
) {
    val programName = programs.firstOrNull { it.id == programId }?.name ?: "Program"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(programName) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                },
                actions = {
                    Row(Modifier.padding(end = 8.dp)) {
                        Text(if (darkMode) "Dark" else "Light", modifier = Modifier.padding(end = 8.dp))
                        Switch(checked = darkMode, onCheckedChange = { onToggleDark() })
                    }
                }
            )
        },
        floatingActionButton = { FloatingActionButton(onClick = onAdd) { Text("+") } }
    ) { padding ->
        if (exercises.isEmpty()) {
            Column(
                Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("No exercises yet.")
                Text("Use + to add your first exercise.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
            ) {
                items(exercises.size) { i ->
                    val e = exercises[i]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onEdit(e.id) }
                            ) {
                                Text(e.title, style = MaterialTheme.typography.titleLarge)
                                Text("${e.sets} sets × ${e.reps} reps")
                            }
                            Row {
                                TextButton(onClick = { onEdit(e.id) }) { Text("Edit") }
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(onClick = { onDelete(e.id) }) { Text("Delete") }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExerciseScreen(
    programId: String,
    existing: Exercise?,
    onSave: (title: String, sets: Int, reps: Int, id: Long?) -> String?,
    onCancel: () -> Unit,
    darkMode: Boolean,
    onToggleDark: () -> Unit
) {
    var title by remember { mutableStateOf(existing?.title ?: "") }
    var setsText by remember { mutableStateOf(existing?.sets?.toString() ?: "3") }
    var repsText by remember { mutableStateOf(existing?.reps?.toString() ?: "10") }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existing == null) "Add exercise" else "Edit exercise") },
                navigationIcon = {
                    TextButton(onClick = onCancel) { Text("Back") }
                },
                actions = {
                    Row(Modifier.padding(end = 8.dp)) {
                        Text(if (darkMode) "Dark" else "Light", modifier = Modifier.padding(end = 8.dp))
                        Switch(checked = darkMode, onCheckedChange = { onToggleDark() })
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Exercise name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = setsText,
                onValueChange = { setsText = it },
                label = { Text("Sets (number)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = repsText,
                onValueChange = { repsText = it },
                label = { Text("Reps (number)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = {
                val sets = setsText.toIntOrNull() ?: -1
                val reps = repsText.toIntOrNull() ?: -1
                val err = onSave(title, sets, reps, existing?.id)
                if (err != null) {
                    error = err
                }
            }) {
                Text("Save")
            }
        }
    }
}





