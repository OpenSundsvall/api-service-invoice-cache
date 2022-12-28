package se.sundsvall.invoicecache.integration.raindance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Result from raindance.
 */
@Builder(setterPrefix = "with")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RaindanceQueryResultDto {
    
    @Column(name = "NR")            //numeric(15,0)
    private int nr;
    
    @Column(name = "KUNDID_TEXT", length = 40)   //varchar40
    private String kundidText;
    
    @Column(name = "KUNDID", length = 16)    //varchar16
    private String kundid;
    
    @Column(name = "ORGNR", length = 40)     //varchar40
    private String orgnr;
    
    @Column(name = "KUNDRTYP", length = 2)      //varchar2
    private String kundrtyp;
    
    @Column(name = "BELOPP_SEK", precision = 2)    //numeric(15,2)
    private BigDecimal beloppSek;
    
    @Column(name = "BETALT_SEK", precision = 2)    //numeric(15,2)
    private BigDecimal betaltSek;
    
    @Column(name = "MOMS_VAL", precision = 2)   //numeric(15,2)
    private BigDecimal momsVal;
    
    @Column(name = "FAKTURADATUM")  //datetime
    private Timestamp fakturadatum;
    
    @Column(name = "FORFALLODATUM") //datetime
    private Timestamp forfallodatum;

    @Column(name = "BETPAMDATUM")    //datetime
    private Timestamp betpamdatum;

    @Column(name = "UTSKRDATUM")   //datetime
    private Timestamp utskrdatum;

    @Column(name = "OCRNR", length = 40)         //varchar40
    private String ocrnr;
    
    @Column(name = "VREF", length = 40)          //varchar40
    private String VREF;
    
    @Column(name = "FAKTURASTATUS", length = 40)    //varchar40
    private String fakturastatus;
    
    @Column(name = "KRAVNIVA", precision = 0)      //numeric(1,0)
    private int kravniva;
    
    @Column(name = "FAKTSTATUS", length = 4)    //varchar4
    private String faktstatus;

    @Column(name = "FAKTSTATUS2", length = 4)   //varchar4
    private String faktstatus2;
    
    @Column(name = "TAB_BEHAND", length = 4)    //varchar4
    private String tabBehand;
    
    @Column(name = "NAMN2", length = 40)     //varchar40
    private String namn2;
    
    @Column(name = "ADR2", length = 40)      //varchar40
    private String adr2;
    
    @Column(name = "ORT", length = 40)       //varchar40
    private String ort;
    
    @Column(name = "Z21_BLOPNR")    //int   from ik.BLOPNR
    private int z21_blopnr;
    
    @Column(name = "Z21_RPNR")      //int   from ik.RPNR
    private int z21_rpnr;
    
    @Column(name = "Z21_BEABEL1", precision = 2)   //decimal(15,2)     from ik.BEABEL1
    private BigDecimal z21_beabel1;
    
    @Column(name = "Z21_BEADAT")    //datetime  from ik.BEADAT
    private Timestamp z21_beadat;
    
    @Column(name = "Z11_BLOPNR")    //int   from it.BLOPNR
    private int z11_blopnr;
    
    @Column(name = "Z11_SBNR")      //int   from it.SBNR
    private int z11_sbnr;
    
    @Column(name = "Z11_BEASUM1", precision = 2)   //decimal(15,2) from it.BEASUM1
    private BigDecimal z11_beasum1;
    
    @Column(name = "Z11_BEARPNR")   //int   from it.BEARPNR
    private int z11_bearpnr;
    
    @Column(name = "Z11_BEADAT")   //datetime   from it.BEADAT
    private Timestamp z11_beadat;

    @Column(name = "Filnamn", length = 40)  //varchar40
    private String filnamn;
}
