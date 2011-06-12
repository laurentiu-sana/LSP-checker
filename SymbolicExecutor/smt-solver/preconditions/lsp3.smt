(benchmark sav11
  :logic AUFNIRA
  :status unknown
  :extrafuns ( (sum Int) )
  :formula 
    ; Superclass: false
    ; Subclass: sum < 100

    (not (implies (not (false)) (not (< sum 100))))

    ; Just for testing, reverse the preconditions
    ;(not (implies (not (< sum 100)) (not (false))))
)

