<template>
    <div class="container">
        <div class="row">
            <div class="col-sm-10">
                <h1>Usuários</h1>
                <hr><br><br>
                <alertcrud :message="message" v-if="showMessage"></alertcrud>
                <button type="button" class="btn btn-success btn-sm"
                        v-b-modal.user-modal
                        @click="addUser()">Adicionar</button>
                <br><br>
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th scope="col">Nome</th>
                            <th scope="col">Idioma</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="(user, index) in users" :key="index">
                            <td>{{ user.name }}</td>
                            <td>{{ user.language_description }}</td>
                            <td>
                                <div class="btn-group" role="group">
                                    <button type="button"
                                            class="btn btn-warning btn-sm"
                                            v-b-modal.user-modal
                                            @click="editUser(user)">
                                        Alterar
                                    </button>
                                    <button type="button"
                                            class="btn btn-danger btn-sm"
                                            @click="onDeleteUser(user)">
                                        Deletar
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <b-modal ref="userModal"
                 id="user-modal"
                 :title=this.modalTitle
                 hide-footer>
          <b-form @submit="onSubmit" @reset="onReset" class="w-100">
            <b-form-group id="form-title-group"
                          label="Nome:"
                          label-for="form-title-input">
              <b-form-input id="form-title-input"
                            type="text"
                            v-model="userForm.name"
                            required
                            placeHolder="Nome">
              </b-form-input>
            </b-form-group>
            <b-form-group id="form-author-group"
                          label="Idioma:"
                          label-for="form-author-input">
              <b-form-select v-model="userForm.language_code" :options="languages">

              </b-form-select>
            </b-form-group>
            <b-button-group>
              <b-button type="submit" variant="primary">Salvar</b-button>
              <b-button type="reset" variant="danger">Cancelar</b-button>
            </b-button-group>
          </b-form>
        </b-modal>

    </div>
</template>

<script>
import axios from 'axios';
import AlertCrud from './Alert.vue';

export default {
  name: 'UsersCrud',
  data() {
    return {
      users: [],
      userForm: {
        id: '',
        name: '',
        language_code: '',
        language_description: '',
      },
      message: '',
      modalTitle: '',
      showMessage: false,
      languages: [
        {value: 'BR', text: 'Português'},
        {value: 'EN', text: 'Inglês'},
        {value: 'SP', text: 'Espanhol'},
      ]
    };
  },
  components: {
    alertcrud: AlertCrud,
  },
  methods: {
    getUsers() {
      const path = 'http://localhost:8080/api/v1/users';
      axios.get(path)
        .then((res) => {
          this.users = res.data;
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
        });
    },
    user(payload) {
      const path = 'http://localhost:8080/api/v1/users';
      axios.post(path, payload)
        .then(() => {
          this.getUsers();
          this.message = 'Usuário incluído!';
          this.showMessage = true;
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.log(error);
          this.getUsers();
        });
    },
    updateUser(payload, userID) {
      const path = `http://localhost:8080/api/v1/users/${userID}`;
      axios.put(path, payload)
        .then(() => {
          this.getUsers();
          this.message = 'Usuário alterado!';
          this.showMessage = true;
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
          this.getUsers();
        });
    },
    removeUser(userID) {
      const path = `http://localhost:8080/api/v1/users/${userID}`;
      axios.delete(path)
        .then(() => {
          this.getUsers();
          this.message = 'Usuário deletado!';
          this.showMessage = true;
        })
        .catch((error) => {
          // eslint-disable-next-line
          console.error(error);
          this.getUsers();
        });
    },
    initForm() {
      this.userForm.id = '';
      this.userForm.name = '';
      this.userForm.language_code = '';
      this.userForm.language_description = '';
    },
    addUser() {
      this.modalTitle = 'Adicionar usuário';
    },
    editUser(user) {
      this.userForm = user;
      this.modalTitle = `Editar usuário ${user.id}`;
    },
    onSubmit(evt) {
      evt.preventDefault();
      this.$refs.userModal.hide();
      const payload = {
        name: this.userForm.name,
        language_code: this.userForm.language_code,
      };
      if (this.userForm.id) {
        this.updateUser(payload, this.userForm.id);
      } else {
        this.user(payload);
      }
      this.initForm();
    },
    onReset(evt) {
      evt.preventDefault();
      this.$refs.userModal.hide();
      this.initForm();
      this.getUsers();
    },
    onDeleteUser(user) {
      // eslint-disable-next-line
      if (confirm('Realmente deseja deletar?')) {
        this.removeUser(user.id);
      }
    },
  },
  created() {
    this.getUsers();
  },
};
</script>
