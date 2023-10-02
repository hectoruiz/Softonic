package com.hectoruiz.data.models

import com.hectoruiz.domain.commons.Constants
import com.hectoruiz.domain.models.Reliability
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AppDataMapperTest {

    @Test
    fun `null response api model to list app model`() {
        val appApiModelList: ResponseApiModel? = null
        val appModelList = appApiModelList.toModel()

        assertTrue(appModelList.isEmpty())
    }

    @Test
    fun `null result api model to list app model`() {
        val resultApiModelList: ResultApiModel? = null
        val appApiModelList = ResponseApiModel(resultApiModelList)
        val appModelList = appApiModelList.toModel()

        assertTrue(appModelList.isEmpty())
    }

    @Test
    fun `null list result api model to list app model`() {
        val resultApiModelList = ResultApiModel(apps = null)
        val appApiModelList = ResponseApiModel(resultApiModelList)
        val appModelList = appApiModelList.toModel()

        assertTrue(appModelList.isEmpty())
    }

    @Test
    fun `empty list api model to list app model`() {
        val resultApiModelList = ResultApiModel(emptyList())
        val appApiModelList = ResponseApiModel(resultApiModelList)
        val appModelList = appApiModelList.toModel()

        assertTrue(appModelList.isEmpty())
    }

    @Test
    fun `null item list api model to list app model`() {
        val resultApiModelList = ResultApiModel(listOf(null))
        val appApiModelList = ResponseApiModel(resultApiModelList)
        val appModelList = appApiModelList.toModel()

        assertTrue(appModelList.size == 1)

        assertEquals("", appModelList[0].name)
        assertEquals("", appModelList[0].packageName)
        assertEquals("", appModelList[0].icon)
        assertEquals(0, appModelList[0].numDownloads)
        assertEquals(0F, appModelList[0].averageStats)
        assertEquals(0, appModelList[0].totalStats)
    }

    @Test
    fun `list api model to list app model`() {
        val appRatingApiModel = AppRatingApiModel(avg = 4.5F, total = 121)
        val appStatsApiModel = AppStatsApiModel(downloads = 231231, rating = appRatingApiModel)
        val appApiModel = AppApiModel(
            name = "name",
            packageName = "packageName",
            icon = "icon",
            stats = appStatsApiModel,
        )
        val resultApiModel = ResultApiModel(apps = listOf(appApiModel))
        val appApiModelList = ResponseApiModel(result = resultApiModel)
        val appModelList = appApiModelList.toModel()

        assertTrue(appModelList.size == 1)

        assertEquals("name", appModelList[0].name)
        assertEquals("packageName", appModelList[0].packageName)
        assertEquals("icon", appModelList[0].icon)
        assertEquals(231231, appModelList[0].numDownloads)
        assertEquals(4.5F, appModelList[0].averageStats)
        assertEquals(121, appModelList[0].totalStats)
    }

    @Test
    fun `null response detail api model to list app detail model`() {
        val responseDetailApiModel: ResponseDetailApiModel? = null
        val appDetailModel = responseDetailApiModel.toModel()

        assertNull(appDetailModel)
    }

    @Test
    fun `null node api model to list app detail model`() {
        val nodeApiModel = null
        val responseDetailApiModel = ResponseDetailApiModel(node = nodeApiModel)
        val appModelList = responseDetailApiModel.toModel()

        assertNull(appModelList)
    }

    @Test
    fun `null meta api model to list app detail model`() {
        val metaApiModel = null
        val nodeApiModel = NodeApiModel(meta = metaApiModel)
        val responseDetailApiModel = ResponseDetailApiModel(node = nodeApiModel)
        val appModelList = responseDetailApiModel.toModel()

        assertNull(appModelList)
    }

    @Test
    fun `null app detail api model to list app detail model`() {
        val appDetailApiModel = null
        val metaApiModel = MetaApiModel(appDetail = appDetailApiModel)
        val nodeApiModel = NodeApiModel(meta = metaApiModel)
        val responseDetailApiModel = ResponseDetailApiModel(node = nodeApiModel)
        val appModelList = responseDetailApiModel.toModel()

        assertNull(appModelList)
    }

    @Test
    fun `app detail null api model to list app detail model`() {
        val appDetailApiModel = AppDetailApiModel()
        val metaApiModel = MetaApiModel(appDetail = appDetailApiModel)
        val nodeApiModel = NodeApiModel(meta = metaApiModel)
        val responseDetailApiModel = ResponseDetailApiModel(node = nodeApiModel)
        val appModelList = responseDetailApiModel.toModel()

        appModelList.apply {
            assertEquals("", this?.name)
            assertEquals("", this?.icon)
            assertEquals("", this?.updated)
            assertEquals("", this?.version)
            assertEquals("", this?.pegi)
            assertEquals(0, this?.size)
            assertEquals("", this?.description)
            assertEquals(Reliability.NO_DATA, this?.reliability)
            assertEquals("", this?.developerName)
            assertEquals("", this?.developerEmail)
            assertEquals("", this?.keywords)
            assertEquals("", this?.path)
        }
    }

    @Test
    fun `app detail api model to list app model`() {
        val appDetailMediaApiModel = AppDetailMediaApiModel(
            keywords = listOf("keyword1", "keyword2"), description = "description",
        )
        val appDetailMalwareApiModel = AppDetailMalwareApiModel(reliability = "TRUSTED")
        val appDetailFileApiModel = AppDetailFileApiModel(
            version = "2.3.0",
            malware = appDetailMalwareApiModel,
            path = "path"
        )
        val appDetailDeveloperApiModel = AppDetailDeveloperApiModel(name = "name", email = "email")
        val appDetailAgeApiModel = AppDetailAgeApiModel(qualification = "qualification")
        val appDetailApiModel =
            AppDetailApiModel(
                name = "name",
                size = 2048,
                icon = "icon",
                updated = "updated",
                age = appDetailAgeApiModel,
                developer = appDetailDeveloperApiModel,
                file = appDetailFileApiModel,
                media = appDetailMediaApiModel,
            )
        val metaApiModel = MetaApiModel(appDetail = appDetailApiModel)
        val nodeApiModel = NodeApiModel(meta = metaApiModel)
        val responseDetailApiModel = ResponseDetailApiModel(node = nodeApiModel)
        val appModelList = responseDetailApiModel.toModel()

        appModelList.apply {
            assertEquals(appDetailApiModel.name, this?.name)
            assertEquals(appDetailApiModel.icon, appModelList?.icon)
            assertEquals(appDetailApiModel.updated, appModelList?.updated)
            assertEquals(appDetailApiModel.file?.version, appModelList?.version)
            assertEquals(appDetailApiModel.age?.qualification, appModelList?.pegi)
            assertEquals((appDetailApiModel.size?.div(Constants.CONVERSION_MB)), appModelList?.size)
            assertEquals(appDetailApiModel.media?.description, appModelList?.description)
            assertEquals(Reliability.TRUSTED, appModelList?.reliability)
            assertEquals(appDetailApiModel.developer?.name, appModelList?.developerName)
            assertEquals(appDetailApiModel.developer?.email, appModelList?.developerEmail)
            assertEquals(
                "#" + appDetailApiModel.media?.keywords?.get(0) + " "
                        + "#" + appDetailApiModel.media?.keywords?.get(1), appModelList?.keywords
            )
            assertEquals(appDetailApiModel.file?.path, this?.path)
        }
    }
}
