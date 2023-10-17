export const airlines = [
  {
    "value": "Air Austral",
    "label": "Air Austral",
    "flight": ["UU102", "UU108", "UU130", "UU104", "UU103", "UU119", "UU105", "UU109"]
  },
  {
    "value": "Air Belgium",
    "label": "Air Belgium",
    "flight": ["KF2301"]
  },
  {
    "value": "Air France",
    "label": "Air France",
    "flight": ["AF7964", "AF7941", "AF470", "AF7943",  "AF473"]
  },
  {
    "value": "Air India",
    "label": "Air India",
    "flight": []
  },
  {
    "value": "Air Madagascar",
    "label": "Air Madagascar",
    "flight": []
  },
  {
    "value": "Air Mauritius",
    "label": "Air Mauritius",
    "flight": ["MK261", "MK015", "MK053", "MK113", "MK145", "MK135", "MK219", "MK911", "MK933", "MK749", "MK289", "MK239", "MK852", "MK120", "MK851",  "MK126", "MK218", "MK130", "MK228", "MK140", "MK238", "MK150", "MK042", "MK014"]
  },
  {
    "value": "Air Seychelles",
    "label": "Air Seychelles",
    "flight": ["HM049"]
  },
  {
    "value": "Austrian Airlines",
    "label": "Austrian Airlines",
    "flight": ["OS017", "OS018"]
  },
  {
    "value": "British Airways",
    "label": "British Airways",
    "flight": ["BA7762", "BA2063", "BA2062", "BA230"]
  },
  {
    "value": "China Eastern",
    "label": "China Eastern",
    "flight": []
  },
  {
    "value": "Comair",
    "label": "Comair",
    "flight": []
  },
  {
    "value": "Condor Flugdienst",
    "label": "Condor Flugdienst",
    "flight": ["DE2314", "DE2315"]
  },
  {
    "value": "Corsair International",
    "label": "Corsair International",
    "flight": ["SS952", "SS953"]
  },
  {
    "value": "Edelweiss Air",
    "label": "Edelweiss Air",
    "flight": ["WK70"]
  },
  {
    "value": "Egypt Air",
    "label": "Egypt Air",
    "flight": []
  },
  {
    "value": "Emirates",
    "label": "Emirates",
    "flight": ["EK701", "EK702", "EK704"]
  },
  {
    "value": "Eurowings",
    "label": "Eurowings",
    "flight": ["4Y153", "4Y152"]
  },
  {
    "value": "Hong Kong Airlines",
    "label": "Hong Kong Airlines",
    "flight": []
  },
  {
    "value": "Kenya Airways",
    "label": "Kenya Airways",
    "flight": ["KQ270", "KQ271"]
  },
  {
    "value": "KLM",
    "label": "KLM",
    "flight": []
  },
  {
    "value": "Lufthansa",
    "label": "Lufthansa",
    "flight": ["LH4516"]
  },
  {
    "value": "Private Jet",
    "label": "Private Jet",
    "flight": []
  },
  {
    "value": "Saudi Arabian Airlines",
    "label": "Saudi Arabian Airlines",
    "flight": []
  },
  {
    "value": "Singapore Airlines",
    "label": "Singapore Airlines",
    "flight": []
  },
  {
    "value": "South African Airways",
    "label": "South African Airways",
    "flight": ["SA7109"]
  },
  {
    "value": "Swiss Airlines",
    "label": "Swiss Airlines",
    "flight": ["LX8070"]
  },
  {
    "value": "TUI Airways",
    "label": "TUI Airways",
    "flight": []
  },
  {
    "value": "Turkish Airlines",
    "label": "Turkish Airlines",
    "flight": ["TK176"]
  },
]


export const getFlightList = (alirline) => {
  const flightList = []
  if (alirline) {
    const flights = airlines.find((e) => e.value === alirline).flight;
    if (flights.length === 0) {
      flightList.push({ "value": "", "label": "" });
    } else {
      flights.forEach(f => flightList.push({ "value": f, "label": f }));
    }

    return flightList.sort((a, b) => a.label.localeCompare(b.label));
  }
}
