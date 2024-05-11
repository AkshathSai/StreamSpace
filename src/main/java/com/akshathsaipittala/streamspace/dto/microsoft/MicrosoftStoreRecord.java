package com.akshathsaipittala.streamspace.dto.microsoft;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MicrosoftStoreRecord (
        String title,
        String description,
        CuratedCollectionDetails curatedCollectionDetails,
        ArrayList<ProductsList> productsList,
        Object highlightedList,
        Object cursor,
        boolean hasCursorPaging,
        Object filterOptions,
        int totalCount,
        int nextPageNumber,
        boolean hasMorePages,
        Object continuationToken) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record CuratedCollectionDetails (
        Object bgColor,
        String description,
        Object fgColor,
        Object imageUrl,
        String title,
        String itemType) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record Badge (
        Object deferredRequestKey,
        String styleKey,
        String text,
        String type) {
}


@JsonIgnoreProperties(ignoreUnknown = true)
record Image (
        String imageType,
        String backgroundColor,
        String foregroundColor,
        String caption,
        String imagePositionInfo,
        Object productColor,
        String url,
        int height,
        int width) {
}

//@JsonIgnoreProperties(ignoreUnknown = true)
//record Image2 (
//        String imageType,
//        String backgroundColor,
//        String foregroundColor,
//        String caption,
//        String imagePositionInfo,
//        Object productColor,
//        String url,
//        int height,
//        int width) {
//}

@JsonIgnoreProperties(ignoreUnknown = true)
record Installer (
        String type,
        String id,
        Object extras,
        Object installerErrorUrl,
        Object installerErrors) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record Preview (
        String $type,
        String imageType,
        String backgroundColor,
        String foregroundColor,
        String caption,
        String imagePositionInfo,
        Object productColor,
        String url,
        int height,
        int width,
        Object title,
        String videoPurpose,
        Object audioEncoding,
        Object videoEncoding,
        Object deepLink,
        Image image,
        int bitrate,
        Object videoPositionInfo,
        Object overlayFiles,
        Object sortOrder) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record SalePrice (
        Object conditions,
        double price,
        String displayPrice,
        String badgeId) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record SkusSummary (
        String skuId,
        Object skuDisplayRanks,
        Object skuTitle,
        Object description,
        Object images,
        Object badges,
        Object colorHexCode,
        Object colorDisplayName,
        double msrp,
        String displayMSRP,
        ArrayList<SalePrice> salePrices,
        Object includedWith,
        StreamingService streamingService) {
}

@JsonIgnoreProperties(ignoreUnknown = true)
record StreamingService (
        String id,
        String displayName,
        Object installer,
        Object deepLink,
        Object productRef,
        Object packageFamilyName,
        String consumptionModel,
        Object channelName) {
}
