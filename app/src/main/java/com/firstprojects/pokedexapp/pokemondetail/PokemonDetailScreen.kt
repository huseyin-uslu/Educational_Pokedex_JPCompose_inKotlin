package com.firstprojects.pokedexapp.pokemondetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.firstprojects.pokedexapp.R
import com.firstprojects.pokedexapp.data.remote.responses.Pokemon
import com.firstprojects.pokedexapp.data.remote.responses.Stat
import com.firstprojects.pokedexapp.data.remote.responses.Type
import com.firstprojects.pokedexapp.util.Resource
import parseStatToAbbr
import parseStatToColor
import parseTypeToColor
import kotlin.math.roundToInt


@Composable
fun PokemonDetailScreen(
    dominantColor : Color,
    pokemonName : String,
    topPadding : Dp = 20.dp,
    pokemonImageSize : Dp = 200.dp,
    viewModel : PokemonDetailViewModel = hiltViewModel(),
    navController : NavController
){
    val pokemonInfo = produceState<Resource<Pokemon>>(initialValue = Resource.Loading()){
        value = viewModel.getPokemonInfo(pokemonName)
    }.value
    Box(modifier = Modifier
        .fillMaxSize()
        .background(dominantColor)
        .padding(bottom = 16.dp)) {

        PokemonDetailTopSection(navController = navController,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .align(Alignment.TopCenter))
        PokemonDetailStateWrapper(
            pokemonInfo = pokemonInfo,
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = topPadding + pokemonImageSize / 2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .shadow(10.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colors.surface)
                .padding(16.dp)
                .align(Alignment.BottomCenter),
            loadingModifier = Modifier
                .size(100.dp)
                .align(Alignment.Center)
                .padding(
                    top = topPadding + pokemonImageSize / 2f,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        )

        Box(contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()) {
            if(pokemonInfo is Resource.Success){
                pokemonInfo.data?.sprites?.let {
                  Image(painter = rememberImagePainter(
                      data = it.frontDefault,
                              builder = {
                          crossfade(true)
                      },
                  ), contentDescription = pokemonName,
                  modifier = Modifier
                      .size(pokemonImageSize)
                      .offset(y = topPadding))
                }
            }
        }
    }
}

@Composable
fun PokemonDetailTopSection(
    navController :NavController,
    modifier : Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier
        .background(Brush.verticalGradient(
            listOf(
                Color.Black,
                Color.Transparent
            )
        ))){
        Icon(
            imageVector =  Icons.Default.ArrowBack,
            tint = MaterialTheme.colors.surface,
             contentDescription = "Turn Back",
        modifier = Modifier
            .size(36.dp)
            .offset(x = 16.dp, y = 16.dp)
            .clickable {
                navController.popBackStack()
            })
    }
}

@Composable fun PokemonDetailStateWrapper(
    pokemonInfo :Resource<Pokemon>,
    modifier : Modifier = Modifier,
    loadingModifier : Modifier = Modifier
){
    when(pokemonInfo){
     is Resource.Success -> {
         PokemonDetailSection(pokemonInfo = pokemonInfo.data!!,
             modifier = modifier
             .offset(y = (-20).dp))
     }
     is Resource.Error -> {
         Text(text = pokemonInfo.message!!,
             color = Color.Red,
             modifier = modifier,
             textAlign = TextAlign.Center)
     }
        is Resource.Loading -> {
            CircularProgressIndicator(
                modifier = loadingModifier,
                color = MaterialTheme.colors.primary
            )
        }
     }
}

@Composable
fun PokemonDetailSection(
    pokemonInfo : Pokemon,
    modifier : Modifier = Modifier
){
    val scrollState = rememberScrollState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .offset(y = 100.dp)
            .verticalScroll(scrollState)
    ){
        Text(text = "#${pokemonInfo.id} ${pokemonInfo.name?.
        replaceFirstChar(transform = Char::uppercase )}",
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            fontSize = 32.sp,
            color = MaterialTheme.colors.onSurface)
        pokemonInfo.types?.let {
            PokemonTypeSection(it)
            PokemonDetailDataSection(
                pokemonWeight = pokemonInfo.weight!!,
                pokemonHeight = pokemonInfo.height!!)
        }
        PokemonBaseStats(pokemonInfo = pokemonInfo)


    }
}
@Composable
fun PokemonTypeSection(
    types : List<Type>
){
    Row (verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)){
        for(type in types){
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .clip(CircleShape)
                    .background(parseTypeToColor(type))
                    .height(35.dp))
            {
                type.type?.name?.replaceFirstChar(transform = Char::uppercase)?.let {text ->
                    Text(
                        text = text,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}


@Composable
fun PokemonDetailDataSection (
    pokemonWeight : Int,
    pokemonHeight : Int,
    sectionHeight : Dp = 80.dp
) {

    val pokemonWeightInKg = remember {
        (pokemonWeight * 100f).roundToInt() / 1000f
    }
    val pokemonHeightInMeters = remember {
        (pokemonHeight * 100f).roundToInt() / 1000f
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        PokemonDetailDataItem(
            dataValue = pokemonWeightInKg,
            dataUnit = "kg",
            dataIcon = painterResource(id = R.drawable.ic_weight),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier
            .size(1.dp, sectionHeight)
            .background(Color.LightGray))
        PokemonDetailDataItem(
            dataValue = pokemonHeightInMeters,
            dataUnit = "m",
            dataIcon = painterResource(id = R.drawable.ic_height),
            modifier = Modifier.weight(1f)
        )
    }

}

@Composable
fun PokemonDetailDataItem(
    dataValue: Float,
    dataUnit: String,
    dataIcon: Painter,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Icon(painter = dataIcon, contentDescription = null, tint = MaterialTheme.colors.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$dataValue$dataUnit",
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Composable
fun PokemonStat(
    statName : String,
    statValue : Int,
    statMaxValue : Int,
    statColor : Color,
    height : Dp = 28.dp,
    animDuration : Int = 1000,
    animDelay : Int = 0
    ) {

    var animationPlayed by remember {
        mutableStateOf(false)
    }

    val curPercent = animateFloatAsState(targetValue = if (animationPlayed){
      statValue / statMaxValue.toFloat()
    } else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = animDelay
        )
        )
    LaunchedEffect(key1 = true ){
        animationPlayed = true
    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(height)
        .clip(CircleShape)
        .background(
            if (isSystemInDarkTheme()) {
                Color(0xFF505050)
            } else {
                Color.LightGray
            }
        )
    ){
        Row(horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(curPercent.value)
            .clip(CircleShape)
            .background(statColor)
            .padding(horizontal = 8.dp)) {

            Text(text = statName,fontWeight = FontWeight.Bold)
            Text(text = statValue.toString())
        }
    }
}

@Composable
fun PokemonBaseStats(
    pokemonInfo : Pokemon,
    animDelayPerItem : Int = 100
){


   val maxBaseStat = remember {
       pokemonInfo.stats?.maxOf { stat ->
           stat.baseStat!!
       }
   }
    Column(
        modifier = Modifier.fillMaxWidth()
    ){
        Text(
            text = "Base stats:",
            fontSize = 20.sp,
            color = MaterialTheme.colors.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))

        for(i in pokemonInfo.stats!!.indices){
            val stat = pokemonInfo.stats[i]
            PokemonStat(statName = parseStatToAbbr(stat),
                statValue = stat.baseStat!!,
                statMaxValue = maxBaseStat!!,
                statColor = parseStatToColor(stat),
            animDelay = i * animDelayPerItem)
            Spacer(modifier = Modifier.height(8.dp))
        }

    }



}


