<script setup lang="ts">
  import { ref } from 'vue'

  import ItemCard from '@/components/inventario/ItemCard.vue'
  import AppPage from '@/components/ui/AppPage.vue'
  import AppSearchBar from '@/components/ui/AppSearchBar.vue'
  import AppSelect from '@/components/ui/AppSelect.vue'

  const search = ref('')
  const selectedType = ref('todos')
  const selectedStatus = ref('todos')

  const typeItems = [
    { label: 'Todos os tipos', value: 'todos' },
    { label: 'Componente', value: 'componente' },
    { label: 'Equipamento', value: 'equipamento' },
  ]

  const statusItems = [
    { label: 'Todos', value: 'todos' },
    { label: 'Ativo', value: 'ativo' },
    { label: 'Inativo', value: 'inativo' },
  ]
</script>

<template>
  <AppPage subtitle="Consulte itens disponíveis e detalhes de estoque" title="Inventário">
    <v-row class="mb-6" dense>
      <v-col cols="12" md="6">
        <AppSearchBar
          v-model="search"
          clearable
          hide-details
          placeholder="Buscar por nome ou descrição..."
        />
      </v-col>

      <v-col cols="12" md="3" sm="6">
        <AppSelect
          v-model="selectedType"
          :clearable="false"
          hide-details
          item-title="label"
          item-value="value"
          :items="typeItems"
        />
      </v-col>

      <v-col cols="12" md="3" sm="6">
        <AppSelect
          v-model="selectedStatus"
          :clearable="false"
          hide-details
          item-title="label"
          item-value="value"
          :items="statusItems"
        />
      </v-col>
    </v-row>

    <v-row class="mb-6" dense>
      <v-col
        v-for="n in 7"
        :key="n"
        cols="12"
        lg="3"
        md="4"
        sm="6"
        xl="2"
      >
        <ItemCard
          :available="Math.floor(Math.random() * 201)"
          category="Componente Eletrônico"
          description="Resistor de 10kΩ, ideal para circuitos de baixa potência."
          title="Resistor 10kΩ"
          :total="Math.floor(Math.random() * 200) + 201"
        />
      </v-col>
      <v-col
        v-for="n in 7"
        :key="'equipamento-' + n"
        cols="12"
        lg="3"
        md="4"
        sm="6"
        xl="2"
      >
        <ItemCard
          :available="Math.floor(Math.random() * 11)"
          category="Equipamento de Medição"
          description="Multímetro digital com funções de medição de tensão, corrente e resistência."
          icon="mdi-devices"
          icon-color="orange"
          title="Multímetro Digital"
          :total="Math.floor(Math.random() * 10) + 11"
        />
      </v-col>
    </v-row>
  </AppPage>
</template>
