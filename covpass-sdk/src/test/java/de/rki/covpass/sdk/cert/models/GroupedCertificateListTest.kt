/*
 * (C) Copyright IBM Deutschland GmbH 2021
 * (C) Copyright IBM Corp. 2021
 */

package de.rki.covpass.sdk.cert.models

import com.ensody.reactivestate.test.CoroutineTest
import io.mockk.mockk
import java.time.Instant
import kotlin.test.*

internal class GroupedCertificateListTest : CoroutineTest() {

    private val mapper = CertificateListMapper(mockk(relaxed = true))

    private val name1 = "Hans"
    private val name2 = "Franz"
    private val name3 = "Silke"
    private val date1 = "2021-01-01"
    private val date2 = "2021-02-02"
    private val date3 = "2021-03-03"
    private val idComplete1 = "certComplete1"
    private val idComplete2 = "certComplete2"
    private val idComplete3 = "certComplete3"
    private val idIncomplete1 = "certIncomplete1"
    private val idIncomplete2 = "certIncomplete2"
    private val idIncomplete3 = "certIncomplete3"
    private val vaccinationsIncomplete1 =
        listOf(Vaccination(doseNumber = 1, totalSerialDoses = 2, id = idIncomplete1))
    private val vaccinationsComplete1 =
        listOf(Vaccination(doseNumber = 2, totalSerialDoses = 2, id = idComplete1))
    private val vaccinationsIncomplete2 =
        listOf(Vaccination(doseNumber = 1, totalSerialDoses = 2, id = idIncomplete2))
    private val vaccinationsComplete2 =
        listOf(Vaccination(doseNumber = 2, totalSerialDoses = 2, id = idComplete2))
    private val vaccinationsIncomplete3 =
        listOf(Vaccination(doseNumber = 1, totalSerialDoses = 2, id = idIncomplete3))
    private val vaccinationsComplete3 =
        listOf(Vaccination(doseNumber = 2, totalSerialDoses = 2, id = idComplete3))
    private val certIncomplete1 = CovCertificate(
        name = Name(familyNameTransliterated = name1),
        birthDate = date1,
        vaccinations = vaccinationsIncomplete1,
        validUntil = Instant.now()
    )
    private val certComplete1 = CovCertificate(
        name = Name(familyNameTransliterated = name1),
        birthDate = date1,
        vaccinations = vaccinationsComplete1,
        validUntil = Instant.now()
    )
    private val certIncomplete2 = CovCertificate(
        name = Name(familyNameTransliterated = name2),
        birthDate = date2,
        vaccinations = vaccinationsIncomplete2,
        validUntil = Instant.now()
    )
    private val certComplete2 = CovCertificate(
        name = Name(familyNameTransliterated = name2),
        birthDate = date2,
        vaccinations = vaccinationsComplete2,
        validUntil = Instant.now()
    )
    private val certIncomplete3 = CovCertificate(
        name = Name(familyNameTransliterated = name3),
        birthDate = date3,
        vaccinations = vaccinationsIncomplete3,
        validUntil = Instant.now()
    )
    private val certComplete3 = CovCertificate(
        name = Name(familyNameTransliterated = name3),
        birthDate = date3,
        vaccinations = vaccinationsComplete3,
        validUntil = Instant.now()
    )

    @Test
    fun `Empty CovCertificateList transformed to GroupedCertificatesList and backwards`() = runBlockingTest {
        val originalList = CovCertificateList()

        val groupedCertificatesList = mapper.toGroupedCertificatesList(originalList)
        assertEquals(0, groupedCertificatesList.certificates.size)
        assertNull(groupedCertificatesList.favoriteCertId)

        val covCertificateList = mapper.toCovCertificateList(groupedCertificatesList)
        assertEquals(originalList, covCertificateList)
    }

    @Test
    fun `One element CovCertificateList transformed to GroupedCertificatesList and backwards`() = runBlockingTest {

        val originalList = CovCertificateList(mutableListOf(certComplete1.toCombinedCertLocal()))

        val groupedCertificatesList = mapper.toGroupedCertificatesList(originalList)
        assertEquals(1, groupedCertificatesList.certificates.size)
        assertEquals(
            name1,
            groupedCertificatesList.certificates[0].certificates[0].covCertificate.name.familyNameTransliterated
        )
        assertNull(groupedCertificatesList.favoriteCertId)

        val covCertificateList = mapper.toCovCertificateList(groupedCertificatesList)
        assertEquals(originalList, covCertificateList)
    }

    @Test
    fun `Three single elements CovCertificateList transformed to GroupedCertificatesList and backwards`() =
        runBlockingTest {

            val originalList = CovCertificateList(
                mutableListOf(
                    certComplete1.toCombinedCertLocal(),
                    certComplete2.toCombinedCertLocal(),
                    certComplete3.toCombinedCertLocal()
                )
            )

            val groupedCertificatesList = mapper.toGroupedCertificatesList(originalList)
            assertEquals(3, groupedCertificatesList.certificates.size)
            assertEquals(
                name1,
                groupedCertificatesList.certificates[0]
                    .getMainCertificate().covCertificate.name.familyNameTransliterated
            )
            assertEquals(
                name2,
                groupedCertificatesList.certificates[1]
                    .getMainCertificate().covCertificate.name.familyNameTransliterated
            )
            assertEquals(
                name3,
                groupedCertificatesList.certificates[2]
                    .getMainCertificate().covCertificate.name.familyNameTransliterated
            )
            assertNull(groupedCertificatesList.favoriteCertId)

            val covCertificateList = mapper.toCovCertificateList(groupedCertificatesList)
            assertEquals(originalList, covCertificateList)
        }

    @Test
    fun `Two matching element CovCertificateList transformed to GroupedCertificatesList and backwards`() =
        runBlockingTest {

            val originalList = CovCertificateList(
                mutableListOf(certIncomplete1.toCombinedCertLocal(), certComplete1.toCombinedCertLocal())
            )

            val groupedCertificatesList = mapper.toGroupedCertificatesList(originalList)
            assertEquals(1, groupedCertificatesList.certificates.size)
            assertEquals(
                name1,
                groupedCertificatesList.certificates[0].certificates[0].covCertificate.name.familyNameTransliterated
            )
            assertEquals(
                name1,
                groupedCertificatesList.certificates[0].certificates[1].covCertificate.name.familyNameTransliterated
            )
            assertNull(groupedCertificatesList.favoriteCertId)

            val covCertificateList = mapper.toCovCertificateList(groupedCertificatesList)
            assertEquals(originalList, covCertificateList)
        }

    @Test
    fun `Six matching element CovCertificateList transformed to GroupedCertificatesList and backwards`() =
        runBlockingTest {

            val originalList = CovCertificateList(
                mutableListOf(
                    certIncomplete1.toCombinedCertLocal(),
                    certComplete1.toCombinedCertLocal(),
                    certIncomplete2.toCombinedCertLocal(),
                    certComplete2.toCombinedCertLocal(),
                    certIncomplete3.toCombinedCertLocal(),
                    certComplete3.toCombinedCertLocal()
                )
            )

            val groupedCertificatesList = mapper.toGroupedCertificatesList(originalList)
            assertEquals(3, groupedCertificatesList.certificates.size)
            assertEquals(
                name1,
                groupedCertificatesList.certificates[0].certificates[0].covCertificate.name.familyNameTransliterated
            )
            assertEquals(
                name1,
                groupedCertificatesList.certificates[0].certificates[1].covCertificate.name.familyNameTransliterated
            )
            assertEquals(
                name2,
                groupedCertificatesList.certificates[1].certificates[0].covCertificate.name.familyNameTransliterated
            )
            assertEquals(
                name2,
                groupedCertificatesList.certificates[1].certificates[1].covCertificate.name.familyNameTransliterated
            )
            assertEquals(
                name3,
                groupedCertificatesList.certificates[2].certificates[0].covCertificate.name.familyNameTransliterated
            )
            assertEquals(
                name3,
                groupedCertificatesList.certificates[2].certificates[1].covCertificate.name.familyNameTransliterated
            )
            assertNull(groupedCertificatesList.favoriteCertId)

            val covCertificateList = mapper.toCovCertificateList(groupedCertificatesList)
            assertEquals(originalList, covCertificateList)
        }

    @Test
    fun `Two matching and two single elements transformed to GroupedCertificatesList and backwards`() =
        runBlockingTest {

            val originalList = CovCertificateList(
                mutableListOf(
                    certIncomplete1.toCombinedCertLocal(),
                    certIncomplete2.toCombinedCertLocal(),
                    certComplete2.toCombinedCertLocal(),
                    certComplete3.toCombinedCertLocal()
                )
            )

            val groupedCertificatesList = mapper.toGroupedCertificatesList(originalList)
            assertEquals(3, groupedCertificatesList.certificates.size)

            assertEquals(1, groupedCertificatesList.certificates[0].certificates.size)
            assertEquals(
                name1,
                groupedCertificatesList.certificates[0].certificates[0].covCertificate.name.familyNameTransliterated
            )

            assertEquals(2, groupedCertificatesList.certificates[1].certificates.size)
            assertEquals(
                name2,
                groupedCertificatesList.certificates[1].certificates[0].covCertificate.name.familyNameTransliterated
            )
            assertEquals(
                name2,
                groupedCertificatesList.certificates[1].certificates[1].covCertificate.name.familyNameTransliterated
            )

            assertEquals(1, groupedCertificatesList.certificates[2].certificates.size)
            assertEquals(
                name3,
                groupedCertificatesList.certificates[2].certificates[0].covCertificate.name.familyNameTransliterated
            )
            assertNull(groupedCertificatesList.favoriteCertId)

            val covCertificateList = mapper.toCovCertificateList(groupedCertificatesList)
            assertEquals(originalList, covCertificateList)
        }

    @Test
    fun `Ensure favoriteId is set to main certificate after transformation`() = runBlockingTest {
        val testId = GroupedCertificatesId(Name(name1), birthDate = "2011-11-11")

        val originalList = CovCertificateList(
            mutableListOf(
                certIncomplete1.toCombinedCertLocal(),
                certComplete1.toCombinedCertLocal()
            ),
            testId
        )

        val groupedCertificatesList = mapper.toGroupedCertificatesList(originalList)
        assertEquals(1, groupedCertificatesList.certificates.size)
        assertEquals(
            name1,
            groupedCertificatesList.certificates[0].certificates[0].covCertificate.name.familyNameTransliterated
        )
        assertEquals(
            name1,
            groupedCertificatesList.certificates[0].certificates[1].covCertificate.name.familyNameTransliterated
        )
        assertEquals(testId, groupedCertificatesList.favoriteCertId)

        val covCertificateList = mapper.toCovCertificateList(groupedCertificatesList)
        assertEquals(originalList.certificates, covCertificateList.certificates)
        assertEquals(testId, covCertificateList.favoriteCertId)
    }

    @Test
    fun `getCombinedCertificate on empty list returns null`() {
        val emptyList = GroupedCertificatesList()
        assertNull(emptyList.getCombinedCertificate(idComplete1))
    }

    @Test
    fun `getCombinedCertificate on full list returns correct results`() = runBlockingTest {
        val originalList = CovCertificateList(
            mutableListOf(
                certIncomplete1.toCombinedCertLocal(),
                certComplete1.toCombinedCertLocal(),
                certIncomplete2.toCombinedCertLocal(),
                certComplete2.toCombinedCertLocal(),
                certIncomplete3.toCombinedCertLocal(),
                certComplete3.toCombinedCertLocal()
            )
        )

        val groupedCertificatesList = mapper.toGroupedCertificatesList(originalList)

        assertEquals(
            certIncomplete1.toCombinedCertLocal()
                .toCombinedCovCertificate(CertValidationResult.ExpiryPeriod),
            groupedCertificatesList.getCombinedCertificate(idIncomplete1)
        )
        assertEquals(
            certComplete1.toCombinedCertLocal()
                .toCombinedCovCertificate(CertValidationResult.ExpiryPeriod),
            groupedCertificatesList.getCombinedCertificate(idComplete1)
        )
        assertEquals(
            certIncomplete2.toCombinedCertLocal()
                .toCombinedCovCertificate(CertValidationResult.ExpiryPeriod),
            groupedCertificatesList.getCombinedCertificate(idIncomplete2)
        )
        assertEquals(
            certComplete2.toCombinedCertLocal()
                .toCombinedCovCertificate(CertValidationResult.ExpiryPeriod),
            groupedCertificatesList.getCombinedCertificate(idComplete2)
        )
        assertEquals(
            certIncomplete3.toCombinedCertLocal()
                .toCombinedCovCertificate(CertValidationResult.ExpiryPeriod),
            groupedCertificatesList.getCombinedCertificate(idIncomplete3)
        )
        assertEquals(
            certComplete3.toCombinedCertLocal()
                .toCombinedCovCertificate(CertValidationResult.ExpiryPeriod),
            groupedCertificatesList.getCombinedCertificate(idComplete3)
        )
    }

    @Test
    fun `getCombinedCertificate on half filled list returns correct results`() = runBlockingTest {
        val originalList = CovCertificateList(
            mutableListOf(
                certIncomplete1.toCombinedCertLocal(),
                certComplete2.toCombinedCertLocal(),
                certIncomplete3.toCombinedCertLocal(),
                certComplete3.toCombinedCertLocal()
            )
        )

        val groupedCertificatesList = mapper.toGroupedCertificatesList(originalList)

        assertEquals(
            certIncomplete1.toCombinedCertLocal()
                .toCombinedCovCertificate(CertValidationResult.ExpiryPeriod),
            groupedCertificatesList.getCombinedCertificate(idIncomplete1)
        )
        assertNull(groupedCertificatesList.getCombinedCertificate(idComplete1))
        assertNull(groupedCertificatesList.getCombinedCertificate(idIncomplete2))
        assertEquals(
            certComplete2.toCombinedCertLocal()
                .toCombinedCovCertificate(CertValidationResult.ExpiryPeriod),
            groupedCertificatesList.getCombinedCertificate(idComplete2)
        )
        assertEquals(
            certIncomplete3.toCombinedCertLocal()
                .toCombinedCovCertificate(CertValidationResult.ExpiryPeriod),
            groupedCertificatesList.getCombinedCertificate(idIncomplete3)
        )
        assertEquals(
            certComplete3.toCombinedCertLocal()
                .toCombinedCovCertificate(CertValidationResult.ExpiryPeriod),
            groupedCertificatesList.getCombinedCertificate(idComplete3)
        )
    }

    @Test
    fun `deleteCovCertificate on empty list returns false`() {
        val emptyList = GroupedCertificatesList()
        assertFalse(emptyList.deleteCovCertificate(idComplete1))
    }

    @Test
    fun `deleteCovCertificate with non-existent id returns false`() = runBlockingTest {
        val originalList = CovCertificateList(
            mutableListOf(
                certIncomplete1.toCombinedCertLocal(),
                certComplete2.toCombinedCertLocal(),
                certIncomplete3.toCombinedCertLocal(),
                certComplete3.toCombinedCertLocal()
            )
        )

        val groupedCertificatesList = mapper.toGroupedCertificatesList(originalList)

        assertFalse(groupedCertificatesList.deleteCovCertificate(idComplete1))
    }

    @Test
    fun `deleteCovCertificate for all elements results in empty list`() = runBlockingTest {
        val originalList = CovCertificateList(
            mutableListOf(
                certIncomplete1.toCombinedCertLocal(),
                certComplete1.toCombinedCertLocal(),
                certIncomplete2.toCombinedCertLocal(),
                certComplete2.toCombinedCertLocal(),
                certIncomplete3.toCombinedCertLocal(),
                certComplete3.toCombinedCertLocal()
            )
        )

        val groupedCertificatesList = mapper.toGroupedCertificatesList(originalList)

        assertFalse(groupedCertificatesList.deleteCovCertificate(idIncomplete1))
        assertTrue(groupedCertificatesList.deleteCovCertificate(idComplete1))
        assertFalse(groupedCertificatesList.deleteCovCertificate(idComplete2))
        assertTrue(groupedCertificatesList.deleteCovCertificate(idIncomplete2))
        assertFalse(groupedCertificatesList.deleteCovCertificate(idIncomplete3))
        assertTrue(groupedCertificatesList.deleteCovCertificate(idComplete3))
        assertEquals(GroupedCertificatesList(), groupedCertificatesList)
    }

    @Test
    fun `deleteCovCertificate for some elements results in partial list`() = runBlockingTest {
        val originalList = CovCertificateList(
            mutableListOf(
                certIncomplete1.toCombinedCertLocal(),
                certComplete1.toCombinedCertLocal(),
                certIncomplete2.toCombinedCertLocal(),
                certComplete2.toCombinedCertLocal(),
                certIncomplete3.toCombinedCertLocal(),
                certComplete3.toCombinedCertLocal()
            )
        )

        val groupedCertificatesList = mapper.toGroupedCertificatesList(originalList)

        assertFalse(groupedCertificatesList.deleteCovCertificate(idIncomplete1))
        assertFalse(groupedCertificatesList.deleteCovCertificate(idComplete2))
        assertTrue(groupedCertificatesList.deleteCovCertificate(idIncomplete2))
        assertEquals(2, groupedCertificatesList.certificates.size)

        val certificatesIncomplete1 = groupedCertificatesList.getGroupedCertificates(
            GroupedCertificatesId(certIncomplete1.name, certIncomplete1.birthDate)
        )
        val certificatesComplete1 = groupedCertificatesList.getGroupedCertificates(
            GroupedCertificatesId(certComplete1.name, certComplete1.birthDate)
        )
        assertEquals(certificatesComplete1, certificatesIncomplete1)
        assertEquals(1, certificatesComplete1?.certificates?.size)
        assertEquals(
            certComplete1.toCombinedCertLocal()
                .toCombinedCovCertificate(CertValidationResult.ExpiryPeriod),
            certificatesComplete1?.certificates?.get(0)
        )

        assertNull(
            groupedCertificatesList.getGroupedCertificates(
                GroupedCertificatesId(certIncomplete2.name, certIncomplete2.birthDate)
            )
        )
        assertNull(
            groupedCertificatesList.getGroupedCertificates(
                GroupedCertificatesId(certComplete2.name, certComplete2.birthDate)
            )
        )

        val certificatesIncomplete3 = groupedCertificatesList.getGroupedCertificates(
            GroupedCertificatesId(certIncomplete3.name, certIncomplete3.birthDate)
        )
        val certificatesComplete3 = groupedCertificatesList.getGroupedCertificates(
            GroupedCertificatesId(certComplete3.name, certComplete3.birthDate)
        )
        assertEquals(certificatesComplete3, certificatesIncomplete3)
        assertEquals(2, certificatesComplete3?.certificates?.size)
        assertEquals(
            certIncomplete3.toCombinedCertLocal()
                .toCombinedCovCertificate(CertValidationResult.ExpiryPeriod),
            certificatesComplete3?.certificates?.get(0)
        )
        assertEquals(
            certComplete3.toCombinedCertLocal()
                .toCombinedCovCertificate(CertValidationResult.ExpiryPeriod),
            certificatesComplete3?.certificates?.get(1)
        )
    }

    @Test
    fun `addNewCertificate to empty list and check favoriteCertId`() = runBlockingTest {
        val originalList = CovCertificateList(
            mutableListOf()
        )
        val groupedCertificatesList = mapper.toGroupedCertificatesList(originalList)
        assertNull(groupedCertificatesList.favoriteCertId)

        groupedCertificatesList.addNewCertificate(
            certIncomplete1.toCombinedCertLocal()
                .toCombinedCovCertificate(CertValidationResult.Valid)
        )
        assertEquals(
            GroupedCertificatesId(certIncomplete1.name, certIncomplete1.birthDate),
            groupedCertificatesList.favoriteCertId
        )

        groupedCertificatesList.addNewCertificate(
            certIncomplete2.toCombinedCertLocal()
                .toCombinedCovCertificate(CertValidationResult.Valid)
        )
        assertEquals(idIncomplete1, groupedCertificatesList.certificates[0].certificates[0].covCertificate.dgcEntry.id)
        assertEquals(idIncomplete2, groupedCertificatesList.certificates[1].certificates[0].covCertificate.dgcEntry.id)
    }

    private fun CovCertificate.toCombinedCertLocal() =
        CombinedCovCertificateLocal(this, "")
}
