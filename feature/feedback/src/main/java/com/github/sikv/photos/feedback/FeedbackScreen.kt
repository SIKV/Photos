package com.github.sikv.photos.feedback

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.github.sikv.photos.feedback.domain.RequestStatus

private val emailKeyboardOptions = KeyboardOptions(
    keyboardType = KeyboardType.Email,
    imeAction = ImeAction.Next
)

private const val descriptionMaxLines = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FeedbackScreen(
    requestStatus: RequestStatus,
    email: String,
    description: String,
    onEmailChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSubmitPressed: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    if (requestStatus == RequestStatus.Success) {
        onBackPressed()
    }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = { Text(stringResource(id = R.string.send_feedback)) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back_24dp),
                            contentDescription = stringResource(id = R.string.c_d_back)
                        )
                    }
                },
                actions = {
                    if (requestStatus == RequestStatus.InProgress) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(24.dp)
                        )
                    } else {
                        IconButton(onClick = onSubmitPressed) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_send_24dp),
                                contentDescription = stringResource(id = R.string.c_d_submit)
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier
            .statusBarsPadding()
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(dimensionResource(id = R.dimen.paddingTwo))
        ) {
            // TODO Add max length.
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChanged,
                label = { Text(stringResource(id = R.string.enter_your_email)) },
                singleLine = true,
                keyboardOptions = emailKeyboardOptions,
                isError = requestStatus == RequestStatus.InvalidEmail,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(
                modifier = Modifier
                    .height(dimensionResource(id = R.dimen.paddingThree))
            )
            // TODO Add max length.
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChanged,
                label = { Text(stringResource(id = R.string.send_feedback_description)) },
                maxLines = descriptionMaxLines,
                isError = requestStatus == RequestStatus.InvalidDescription,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}
