package com.firstprojects.pokedexapp.pokemonlist



import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.EventListener
import coil.ImageLoader
import coil.bitmap.BitmapPool
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.size.Size
import coil.target.Target
import coil.transform.CircleCropTransformation
import coil.transform.Transformation
import com.firstprojects.pokedexapp.R
import com.firstprojects.pokedexapp.data.models.PokedexListEntry
import com.firstprojects.pokedexapp.ui.theme.RobotoCondensed
import com.firstprojects.pokedexapp.ui.theme.lightGrey
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun PokemonListScreen(navController : NavController,viewModel : PokemonListViewModel = hiltViewModel()){

    Surface(
        color    = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ){
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Image(imageVector = ImageVector.vectorResource(id = R.drawable.ic_international_pok_mon_logo),
               contentDescription = "Pokemon"
           ,modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally))
            SearchBar(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),hint = "Search.."){

                viewModel.searchPokemonList(it)
            }
            Spacer(modifier = Modifier.height(16.dp))
            PokemonList(navController = navController)
        }

    }
}

@Composable
fun SearchBar(
    modifier : Modifier = Modifier,
    hint : String = "",
    onSearch : (String) -> Unit) {

    var text by remember {
        mutableStateOf("")
    }

    var isHintDisplayed by remember{
        mutableStateOf(hint != "")
    }
    Box(modifier = modifier) {


        BasicTextField(

            value = text ,
            onValueChange = {
            text = it
            onSearch(it) },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused && text.isNotEmpty()
                }
        )

        if(isHintDisplayed){
            Text(
                hint,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 20.dp,vertical = 12.dp)
            )
        }
    }
}


@Composable
fun PokemonList(
    navController : NavController,
    viewModel : PokemonListViewModel = hiltViewModel()
){

    val pokemonList by remember { viewModel.pokemonList }
    val endReached  by remember { viewModel.endReached }
    val loadError   by remember { viewModel.loadError }
    val isLoading   by remember { viewModel.isLoading }
    val isSearching by remember { viewModel.isSearching}

    LazyColumn(contentPadding = PaddingValues(16.dp)){
       val  itemCount = if (pokemonList.size % 2 == 0) {
            pokemonList.size / 2
        }else{
            pokemonList.size / 2 + 1
        }

        items(itemCount){lazycolumn ->


            if(lazycolumn >= itemCount -1 && !endReached && !isLoading && !isSearching){
                LaunchedEffect(key1 = true) {
                    viewModel.loadPokemonPaginated()
                }
            }
            PokedexRow(rowID = lazycolumn, entries = pokemonList , navController =  navController )
        }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if(isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        }
        if(loadError.isNotEmpty()) {
            RetrySection(error = loadError) {
                viewModel.loadPokemonPaginated()
            }
        }
    }
}



@Composable
fun PokedexEntry(
    entry: PokedexListEntry,
    navController : NavController,
    modifier : Modifier = Modifier,
    viewModel : PokemonListViewModel = hiltViewModel()
    ){

    val defaultDominantColor = lightGrey
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominantColor
                    )
                )
            )
            .clickable {
                navController.navigate(
                    "pokemon_detail_screen/${dominantColor.toArgb()}/${entry.pokemonName}"
                )
            }
    ) {
        Column() {
            CoilImage(
                imageRequest = ImageRequest.Builder(LocalContext.current)
                    .transformations(object : Transformation {
                        override fun key(): String {
                           return entry.imageUrl
                        }

                        override suspend fun transform(
                            pool: BitmapPool,
                            input: Bitmap,
                            size: Size
                        ): Bitmap {
                                viewModel.calcDominantColor(input.toDrawable(Resources.getSystem())) { color ->
                                    dominantColor = color
                                }
                            return input}

                    })
                    .crossfade(true)
                    .lifecycle(LocalLifecycleOwner.current)
                    .data(entry.imageUrl)
                    .build(),

               imageLoader = ImageLoader.Builder(LocalContext.current)
                    .availableMemoryPercentage(0.25)
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.Crop,
                circularRevealedEnabled = true,
                contentDescription = entry.pokemonName,
                alignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
            )
            Text(
                text = entry.pokemonName,
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PokedexRow(

    rowID : Int,
    entries : List<PokedexListEntry>,
    navController : NavController

){

    Column {
        Row {
            PokedexEntry(entry = entries[rowID * 2],
                navController = navController,
                modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(16.dp))
            if(entries.size >= rowID * 2 + 2){
                PokedexEntry(entry = entries[rowID * 2 + 1],
                    navController = navController,
                modifier = Modifier.weight(1f))
            }else{
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit
) {
    Column {
        Text(error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = "Retry")
        }
    }
}
