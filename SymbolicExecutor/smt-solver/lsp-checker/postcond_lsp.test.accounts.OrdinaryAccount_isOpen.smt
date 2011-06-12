(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrasorts ( Ref )
  :extrafuns ( (assertion_disabled Int) )
  :extrafuns (  )
  :extrafuns (  ( input.isOpen Int )  )
  :extrafuns (  )
  :extrafuns (  ( x Int )  )
  :formula
    (not (or  (and  true   true   (=  input.isOpen   input.isOpen  ) ) (and  true   (and ( = assertion_disabled 0) ( (not ( =  assertion_disabled 0  ) )) )   (=  0   0  ) ) (and  true   (and ( = assertion_disabled 0) (and ( >= x 100) ( = assertion_disabled 0 ) ) )   (=  0   0  ) ) (and  ( = assertion_disabled 0 )   true   (=  0   input.isOpen  ) ) (and  ( = assertion_disabled 0 )   (and ( = assertion_disabled 0) ( (not ( =  assertion_disabled 0  ) )) )   (=  0   0  ) ) (and  ( = assertion_disabled 0 )   (and ( = assertion_disabled 0) (and ( >= x 100) ( = assertion_disabled 0 ) ) )   (=  0   0  ) )) )
)
